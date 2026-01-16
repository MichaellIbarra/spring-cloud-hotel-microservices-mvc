#!/bin/bash

# =============================================================================
# HOTEL MICROSERVICES - DOCKER IMAGE PUSHER
# Automatiza el push de todas las imágenes Docker del proyecto a Docker Hub
# =============================================================================

set -e  # Salir si cualquier comando falla

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuración
DOCKER_USERNAME="michaellibarra"
VERSION="latest"
PUSH_PARALLEL=false
LOGIN_REQUIRED=true
DRY_RUN=false

# Función para mostrar ayuda
show_help() {
    echo -e "${CYAN}Hotel Microservices Docker Pusher${NC}"
    echo ""
    echo "Uso: $0 [opciones]"
    echo ""
    echo "Opciones:"
    echo "  -u, --username USER    Username de Docker Hub (default: michaellibarra)"
    echo "  -v, --version VERSION  Versión del tag (default: latest)"
    echo "  -p, --parallel         Push imágenes en paralelo"
    echo "  -n, --no-login         Saltar verificación de login (ya logueado)"
    echo "  -d, --dry-run          Simular push sin ejecutar"
    echo "  -h, --help             Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0                                 # Push básico"
    echo "  $0 -u myuser -v v1.0.0           # Con usuario y versión específicos"
    echo "  $0 --parallel --no-login         # Paralelo sin login"
    echo "  $0 --dry-run                     # Simular operación"
}

# Procesar argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        -u|--username)
            DOCKER_USERNAME="$2"
            shift 2
            ;;
        -v|--version)
            VERSION="$2"
            shift 2
            ;;
        -p|--parallel)
            PUSH_PARALLEL=true
            shift
            ;;
        -n|--no-login)
            LOGIN_REQUIRED=false
            shift
            ;;
        -d|--dry-run)
            DRY_RUN=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            echo -e "${RED}Opción desconocida: $1${NC}"
            show_help
            exit 1
            ;;
    esac
done

# Función para logging
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${PURPLE}[STEP]${NC} $1"
}

log_dry_run() {
    echo -e "${CYAN}[DRY-RUN]${NC} $1"
}

# Verificar que Docker está instalado y funcionando
check_docker() {
    log_step "Verificando Docker..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker no está instalado"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        log_error "Docker daemon no está ejecutándose"
        exit 1
    fi
    
    log_success "Docker verificado correctamente"
}

# Verificar login en Docker Hub
check_docker_login() {
    if [ "$LOGIN_REQUIRED" = true ]; then
        log_step "Verificando autenticación en Docker Hub..."
        
        # Verificar si ya está logueado
        if docker system info | grep -q "Username: $DOCKER_USERNAME"; then
            log_success "Ya autenticado como: $DOCKER_USERNAME"
        else
            log_warning "No autenticado en Docker Hub"
            log_info "Iniciando sesión..."
            
            if [ "$DRY_RUN" = true ]; then
                log_dry_run "Se requeriría login en Docker Hub"
            else
                if ! docker login; then
                    log_error "Error en autenticación"
                    exit 1
                fi
                log_success "Autenticación exitosa"
            fi
        fi
    else
        log_info "Saltando verificación de login"
    fi
}

# Verificar que las imágenes existen localmente
check_images_exist() {
    log_step "Verificando imágenes locales..."
    
    local missing_images=()
    
    for service in "${SERVICES[@]}"; do
        local image_name="${DOCKER_USERNAME}/${service}:${VERSION}"
        
        if ! docker images -q "$image_name" &> /dev/null; then
            missing_images+=("$image_name")
        fi
    done
    
    if [ ${#missing_images[@]} -gt 0 ]; then
        log_error "Imágenes faltantes:"
        for image in "${missing_images[@]}"; do
            echo "  ❌ $image"
        done
        log_info "💡 Ejecuta primero: ./build-images.sh"
        exit 1
    fi
    
    log_success "Todas las imágenes están disponibles localmente"
}

# Push de una imagen individual
push_image() {
    local service=$1
    local image_name="${DOCKER_USERNAME}/${service}:${VERSION}"
    
    log_step "Subiendo $service..."
    
    if [ "$DRY_RUN" = true ]; then
        log_dry_run "docker push $image_name"
        return 0
    fi
    
    # Verificar que la imagen existe
    if ! docker images -q "$image_name" &> /dev/null; then
        log_error "Imagen no encontrada: $image_name"
        return 1
    fi
    
    # Push de la imagen
    if docker push "$image_name"; then
        log_success "✅ $service subido: $image_name"
        return 0
    else
        log_error "❌ Error subiendo $service"
        return 1
    fi
}

# Push de todas las imágenes en secuencia
push_sequential() {
    local failed_pushes=()
    
    for service in "${SERVICES[@]}"; do
        if ! push_image "$service"; then
            failed_pushes+=("$service")
        fi
        
        echo "" # Línea en blanco para separar pushes
    done
    
    # Reportar resultados
    if [ ${#failed_pushes[@]} -eq 0 ]; then
        log_success "🎉 Todas las imágenes se subieron exitosamente"
    else
        log_error "❌ Fallos en: ${failed_pushes[*]}"
        return 1
    fi
}

# Push de todas las imágenes en paralelo
push_parallel() {
    local pids=()
    local failed_pushes=()
    
    log_info "Subiendo imágenes en paralelo..."
    
    # Crear archivos temporales para capturar salida
    local temp_dir=$(mktemp -d)
    
    # Lanzar pushes en paralelo
    for service in "${SERVICES[@]}"; do
        {
            if push_image "$service" > "$temp_dir/$service.log" 2>&1; then
                echo "SUCCESS:$service" > "$temp_dir/$service.status"
            else
                echo "FAILED:$service" > "$temp_dir/$service.status"
            fi
        } &
        pids+=($!)
    done
    
    # Esperar a que terminen todos
    for pid in "${pids[@]}"; do
        wait $pid
    done
    
    # Procesar resultados
    for service in "${SERVICES[@]}"; do
        local status=$(cat "$temp_dir/$service.status" 2>/dev/null || echo "FAILED:$service")
        local log_content=$(cat "$temp_dir/$service.log" 2>/dev/null || echo "No log available")
        
        if [[ $status == "SUCCESS:$service" ]]; then
            log_success "✅ $service"
        else
            log_error "❌ $service"
            echo "$log_content"
            failed_pushes+=("$service")
        fi
    done
    
    # Limpiar archivos temporales
    rm -rf "$temp_dir"
    
    # Reportar resultados finales
    if [ ${#failed_pushes[@]} -eq 0 ]; then
        log_success "🎉 Todas las imágenes se subieron exitosamente"
    else
        log_error "❌ Fallos en: ${failed_pushes[*]}"
        return 1
    fi
}

# Mostrar resumen de configuración
show_config() {
    echo -e "${CYAN}=== CONFIGURACIÓN DE PUSH ===${NC}"
    echo "Docker Username: $DOCKER_USERNAME"
    echo "Versión: $VERSION"
    echo "Push paralelo: $PUSH_PARALLEL"
    echo "Verificar login: $LOGIN_REQUIRED"
    echo "Modo simulación: $DRY_RUN"
    echo "Imágenes a subir: ${#SERVICES[@]}"
    echo ""
}

# Mostrar resumen de imágenes
show_images_summary() {
    echo -e "${CYAN}=== IMÁGENES A SUBIR ===${NC}"
    
    for service in "${SERVICES[@]}"; do
        local image_name="${DOCKER_USERNAME}/${service}:${VERSION}"
        local size=$(docker images --format "table {{.Size}}" "$image_name" 2>/dev/null | tail -1 || echo "N/A")
        echo "📦 $image_name ($size)"
    done
    echo ""
}

# Lista de servicios
declare -a SERVICES=(
    "server-config-server"
    "server-discovery"
    "server-api-gateway"
    "service-auth"
    "service-user"
    "service-hotel"
    "service-grade"
)

# =============================================================================
# FUNCIÓN PRINCIPAL
# =============================================================================
main() {
    echo -e "${CYAN}"
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║              HOTEL MICROSERVICES DOCKER PUSHER              ║"
    echo "║                    Automated Push Script                    ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    
    # Verificaciones previas
    check_docker
    show_config
    
    # Verificar login solo si no es dry-run
    if [ "$DRY_RUN" = false ]; then
        check_docker_login
    fi
    
    # Verificar que las imágenes existen
    if [ "$DRY_RUN" = false ]; then
        check_images_exist
    fi
    
    # Mostrar resumen de imágenes
    show_images_summary
    
    # Confirmar operación si no es dry-run
    if [ "$DRY_RUN" = false ]; then
        read -p "¿Continuar con el push? [y/N]: " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_warning "Operación cancelada"
            exit 0
        fi
    fi
    
    # Registrar tiempo de inicio
    local start_time=$(date +%s)
    
    # Push de imágenes
    if [ "$DRY_RUN" = true ]; then
        log_step "Simulando push de imágenes..."
    else
        log_step "Iniciando push de imágenes..."
    fi
    
    if [ "$PUSH_PARALLEL" = true ]; then
        push_parallel
    else
        push_sequential
    fi
    
    # Calcular tiempo total
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    local minutes=$((duration / 60))
    local seconds=$((duration % 60))
    
    echo ""
    if [ "$DRY_RUN" = true ]; then
        log_success "🏁 Simulación completada en ${minutes}m ${seconds}s"
    else
        log_success "🏁 Push completado en ${minutes}m ${seconds}s"
    fi
    
    echo ""
    log_info "💡 Tus imágenes están ahora disponibles en:"
    echo "   https://hub.docker.com/u/${DOCKER_USERNAME}"
    echo ""
    log_info "💡 Para usar las imágenes:"
    echo "   docker pull ${DOCKER_USERNAME}/service-name:${VERSION}"
}

# Ejecutar función principal
main "$@"
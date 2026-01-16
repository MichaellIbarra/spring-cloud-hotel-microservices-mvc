#!/bin/bash

# =============================================================================
# HOTEL MICROSERVICES - DOCKER IMAGE BUILDER
# Automatiza la construcción de todas las imágenes Docker del proyecto
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
BUILD_PARALLEL=false
CLEAN_OLD=false

# Función para mostrar ayuda
show_help() {
    echo -e "${CYAN}Hotel Microservices Docker Builder${NC}"
    echo ""
    echo "Uso: $0 [opciones]"
    echo ""
    echo "Opciones:"
    echo "  -u, --username USER    Username de Docker Hub (default: michaellibarra)"
    echo "  -v, --version VERSION  Versión del tag (default: latest)"
    echo "  -p, --parallel         Construir imágenes en paralelo"
    echo "  -c, --clean           Limpiar imágenes antiguas antes de construir"
    echo "  -h, --help            Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0                                 # Construcción básica"
    echo "  $0 -u myuser -v v1.0.0           # Con usuario y versión específicos"
    echo "  $0 --parallel --clean            # Paralelo y limpio"
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
            BUILD_PARALLEL=true
            shift
            ;;
        -c|--clean)
            CLEAN_OLD=true
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

# Limpiar imágenes antiguas
clean_old_images() {
    if [ "$CLEAN_OLD" = true ]; then
        log_step "Limpiando imágenes antiguas..."
        
        # Limpiar imágenes sin tag (<none>)
        docker image prune -f
        
        # Limpiar imágenes específicas del proyecto
        for service in "${SERVICES[@]}"; do
            local image_name="${DOCKER_USERNAME}/${service}:${VERSION}"
            if docker images -q "$image_name" &> /dev/null; then
                log_warning "Eliminando imagen antigua: $image_name"
                docker rmi "$image_name" 2>/dev/null || true
            fi
        done
        
        log_success "Limpieza completada"
    fi
}

# Construir una imagen individual
build_image() {
    local service=$1
    local context_dir=$2
    local image_name="${DOCKER_USERNAME}/${service}:${VERSION}"
    
    log_step "Construyendo $service..."
    
    # Verificar que el directorio existe
    if [ ! -d "$context_dir" ]; then
        log_error "Directorio no encontrado: $context_dir"
        return 1
    fi
    
    # Verificar que el Dockerfile existe
    if [ ! -f "$context_dir/Dockerfile" ]; then
        log_error "Dockerfile no encontrado en: $context_dir"
        return 1
    fi
    
    # Construir imagen
    if docker build -t "$image_name" "$context_dir"; then
        log_success "✅ $service construido: $image_name"
        
        # Opcional: hacer push automáticamente
        # docker push "$image_name"
        
        return 0
    else
        log_error "❌ Error construyendo $service"
        return 1
    fi
}

# Construir todas las imágenes en secuencia
build_sequential() {
    local failed_builds=()
    
    for service in "${SERVICES[@]}"; do
        local context_dir="${SERVICE_DIRS[$service]}"
        
        if ! build_image "$service" "$context_dir"; then
            failed_builds+=("$service")
        fi
        
        echo "" # Línea en blanco para separar builds
    done
    
    # Reportar resultados
    if [ ${#failed_builds[@]} -eq 0 ]; then
        log_success "🎉 Todas las imágenes se construyeron exitosamente"
    else
        log_error "❌ Fallos en: ${failed_builds[*]}"
        return 1
    fi
}

# Construir todas las imágenes en paralelo
build_parallel() {
    local pids=()
    local failed_builds=()
    
    log_info "Construyendo imágenes en paralelo..."
    
    # Crear archivos temporales para capturar salida
    local temp_dir=$(mktemp -d)
    
    # Lanzar builds en paralelo
    for service in "${SERVICES[@]}"; do
        local context_dir="${SERVICE_DIRS[$service]}"
        {
            if build_image "$service" "$context_dir" > "$temp_dir/$service.log" 2>&1; then
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
            failed_builds+=("$service")
        fi
    done
    
    # Limpiar archivos temporales
    rm -rf "$temp_dir"
    
    # Reportar resultados finales
    if [ ${#failed_builds[@]} -eq 0 ]; then
        log_success "🎉 Todas las imágenes se construyeron exitosamente"
    else
        log_error "❌ Fallos en: ${failed_builds[*]}"
        return 1
    fi
}

# Mostrar resumen de configuración
show_config() {
    echo -e "${CYAN}=== CONFIGURACIÓN DE CONSTRUCCIÓN ===${NC}"
    echo "Docker Username: $DOCKER_USERNAME"
    echo "Versión: $VERSION"
    echo "Construcción paralela: $BUILD_PARALLEL"
    echo "Limpiar antiguas: $CLEAN_OLD"
    echo "Servicios a construir: ${#SERVICES[@]}"
    echo ""
}

# Lista de servicios y sus directorios
declare -a SERVICES=(
    "server-config-server"
    "server-discovery"
    "server-api-gateway"
    "service-auth"
    "service-user"
    "service-hotel"
    "service-grade"
)

declare -A SERVICE_DIRS=(
    ["server-config-server"]="./server-config-server"
    ["server-discovery"]="./server-discovery"
    ["server-api-gateway"]="./server-api-gateway"
    ["service-auth"]="./service-auth"
    ["service-user"]="./service-user"
    ["service-hotel"]="./service-hotel"
    ["service-grade"]="./service-grade"
)

# =============================================================================
# FUNCIÓN PRINCIPAL
# =============================================================================
main() {
    echo -e "${CYAN}"
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║              HOTEL MICROSERVICES DOCKER BUILDER             ║"
    echo "║                      Automated Build Script                 ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    
    # Verificaciones previas
    check_docker
    show_config
    
    # Limpiar si se solicita
    clean_old_images
    
    # Registrar tiempo de inicio
    local start_time=$(date +%s)
    
    # Construir imágenes
    log_step "Iniciando construcción de imágenes..."
    
    if [ "$BUILD_PARALLEL" = true ]; then
        build_parallel
    else
        build_sequential
    fi
    
    # Calcular tiempo total
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    local minutes=$((duration / 60))
    local seconds=$((duration % 60))
    
    echo ""
    log_success "🏁 Construcción completada en ${minutes}m ${seconds}s"
    
    # Mostrar tamaño de imágenes
    echo ""
    log_info "📊 Resumen de imágenes construidas:"
    echo ""
    docker images | grep "$DOCKER_USERNAME" | grep "$VERSION" || log_warning "No se encontraron imágenes con el patrón especificado"
    
    echo ""
    log_info "💡 Para ejecutar los servicios:"
    echo "   docker-compose --profile all up -d"
    echo ""
    log_info "💡 Para hacer push a Docker Hub:"
    echo "   docker push ${DOCKER_USERNAME}/service-name:${VERSION}"
}

# Ejecutar función principal
main "$@"
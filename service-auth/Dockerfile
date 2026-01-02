# ============ STAGE 1: Build ============
FROM eclipse-temurin:17-jdk AS build

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Dar permisos de ejecución
RUN chmod +x ./mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# ============ STAGE 2: Runtime ============
FROM eclipse-temurin:17-jre AS runtime

# Crear usuario no-root
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Crear directorio de trabajo
WORKDIR /app

# Copiar JAR usando wildcard
COPY --from=build /app/target/*.jar app.jar

# Cambiar ownership del archivo
RUN chown spring:spring app.jar

# Cambiar a usuario no-root
USER spring:spring

# Exponer puerto
EXPOSE 8080

# Comando directo para ejecutar
ENTRYPOINT ["java", "-jar", "app.jar"]
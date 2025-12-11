# --- Etapa 1: Construcción (Build) ---
# Usamos una imagen de Maven para compilar el proyecto
FROM maven:3.8.5-openjdk-17 AS build

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo de configuración de Maven y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto y omitimos los tests para ir más rápido
RUN mvn clean package -DskipTests

# --- Etapa 2: Ejecución (Run) ---
# Usamos la imagen ligera para ejecutar la app (igual que tenías)
FROM eclipse-temurin:17-jdk-alpine

# Volumen para temporales
VOLUME /tmp

# Copiamos el JAR generado en la Etapa 1 (build) a la etapa actual
# Aquí usamos --from=build para referirnos a la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Comando de inicio
ENTRYPOINT ["java","-jar","/app.jar"]
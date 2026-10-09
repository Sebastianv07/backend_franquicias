# ---------- Etapa 1: compilación ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar primero el pom.xml para que Docker guarde en caché las dependencias
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copiar el código fuente y generar el .jar
COPY src ./src
RUN mvn -B package -DskipTests

# ---------- Etapa 2: ejecución ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ejecutar la aplicación con un usuario sin privilegios
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/target/*.jar app.jar

USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

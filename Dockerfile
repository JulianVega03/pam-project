# Stage 1: Construcción de la aplicación
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Imagen para ejecutar la aplicación
FROM openjdk:17-jdk-slim
WORKDIR /app
# Instalar libfreetype6 y otras dependencias necesarias
RUN apt-get update && apt-get install -y libfreetype6 libfontconfig1  --no-install-recommends && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/pam-0.2.4.jar ./pam-0.2.4.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "pam-0.2.4.jar"]

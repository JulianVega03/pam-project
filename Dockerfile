# Stage 1: Construcción de la aplicación
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Imagen para ejecutar la aplicación
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/pam-0.2.4.jar ./pam-0.2.4.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "pam-0.2.4.jar"]

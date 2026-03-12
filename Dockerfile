# -------- build stage --------
FROM maven:3.9.12-eclipse-temurin-25 AS build

WORKDIR /app

# cache dependencias
COPY pom.xml .
RUN mvn -B dependency:go-offline

# copiar codigo
COPY src ./src

# compilar
RUN mvn clean package -DskipTests


# -------- runtime stage --------
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
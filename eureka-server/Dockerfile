FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl eureka-server -am

FROM eclipse-temurin:17-jdk-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /app/eureka-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
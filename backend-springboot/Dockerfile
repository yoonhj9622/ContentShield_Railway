# Build stage
FROM gradle:8.10-jdk21 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Dserver.port=${PORT:8081}", "-jar", "app.jar"]

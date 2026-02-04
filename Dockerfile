# Build Stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Gradle 캐시 활용
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew build -x test --no-daemon || true

COPY src src
RUN ./gradlew clean build -x test --no-daemon

# Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

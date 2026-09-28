# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Cache Gradle wrapper + dependencies separately from sources
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 spring
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

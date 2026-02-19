# -------- Build Stage --------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

COPY . .

# Grant execution rights to gradlew
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean build -x test

# -------- Runtime Stage --------
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

ARG DEPENDENCY=/workspace/app/build/libs
COPY --from=build ${DEPENDENCY}/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app.jar"]

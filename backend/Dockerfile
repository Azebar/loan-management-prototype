## ---------- build stage ----------
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Cache dependencies separately from sources.
COPY gradle ./gradle
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

## ---------- runtime stage ----------
FROM eclipse-temurin:25-jre

WORKDIR /app
RUN useradd --system --uid 10001 spring && chown spring:spring /app
USER spring

COPY --from=build /app/build/libs/app.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

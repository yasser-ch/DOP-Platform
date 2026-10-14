# Build : mvn -pl auth-service -am package  puis  docker build -t dop/auth-service auth-service
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/auth-service-*.jar app.jar
USER 10001
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]

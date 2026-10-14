# Build : mvn -pl reconciliation-service -am package  puis  docker build -t dop/reconciliation-service reconciliation-service
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/reconciliation-service-*.jar app.jar
USER 10001
EXPOSE 8086
ENTRYPOINT ["java","-jar","/app/app.jar"]

# Build : mvn -pl document-service -am package  puis  docker build -t dop/document-service document-service
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/document-service-*.jar app.jar
USER 10001
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]

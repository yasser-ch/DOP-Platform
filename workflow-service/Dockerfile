# Build : mvn -pl workflow-service -am package  puis  docker build -t dop/workflow-service workflow-service
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/workflow-service-*.jar app.jar
USER 10001
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app/app.jar"]

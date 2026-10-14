# Build : mvn -pl reporting-service -am package  puis  docker build -t dop/reporting-service reporting-service
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/reporting-service-*.jar app.jar
USER 10001
EXPOSE 8089
ENTRYPOINT ["java","-jar","/app/app.jar"]

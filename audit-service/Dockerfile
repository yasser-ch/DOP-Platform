# Build : mvn -pl audit-service -am package  puis  docker build -t dop/audit-service audit-service
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/audit-service-*.jar app.jar
USER 10001
EXPOSE 8088
ENTRYPOINT ["java","-jar","/app/app.jar"]

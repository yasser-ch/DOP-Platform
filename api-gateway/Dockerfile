# Build : mvn -pl api-gateway -am package  puis  docker build -t dop/api-gateway api-gateway
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/api-gateway-*.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

# Build : mvn -pl declaration-service -am package  puis  docker build -t dop/declaration-service declaration-service
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/declaration-service-*.jar app.jar
USER 10001
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]

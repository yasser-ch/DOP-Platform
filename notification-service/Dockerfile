# Build : mvn -pl notification-service -am package  puis  docker build -t dop/notification-service notification-service
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/notification-service-*.jar app.jar
USER 10001
EXPOSE 8087
ENTRYPOINT ["java","-jar","/app/app.jar"]

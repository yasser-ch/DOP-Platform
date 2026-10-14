# Build : mvn -pl assujettis-service -am package  puis  docker build -t dop/assujettis-service assujettis-service
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY target/assujettis-service-*.jar app.jar
USER 10001
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]

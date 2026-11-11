# Build : docker build -f assujettis-service/Dockerfile -t dop/assujettis-service .   (depuis la racine)

# ---------- Étape 1 : compilation ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -B -pl assujettis-service -am package -DskipTests

# ---------- Étape 2 : image finale ----------
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY --from=build /build/assujettis-service/target/assujettis-service-*.jar app.jar
USER 10001
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]

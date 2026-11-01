# Build : docker build -f api-gateway/Dockerfile -t dop/api-gateway .   (depuis la racine)

# ---------- Étape 1 : compilation ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -B -pl api-gateway -am package -DskipTests

# ---------- Étape 2 : image finale ----------
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY --from=build /build/api-gateway/target/api-gateway-*.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

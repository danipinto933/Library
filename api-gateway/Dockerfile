FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app
COPY api-gateway/.mvn/ .mvn
COPY api-gateway/mvnw api-gateway/pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
COPY api-gateway/src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

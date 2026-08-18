FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app
COPY discovery-server/.mvn/ .mvn
COPY discovery-server/mvnw discovery-server/pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
COPY discovery-server/src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]

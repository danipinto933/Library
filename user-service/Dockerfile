FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY user-service/.mvn/ .mvn
COPY user-service/mvnw user-service/pom.xml ./
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
COPY user-service/src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

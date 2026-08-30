FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app
COPY book-service/.mvn/ .mvn
COPY book-service/mvnw book-service/pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
COPY book-service/src ./src
COPY book-service/uploads ./uploads
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/uploads ./uploads
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

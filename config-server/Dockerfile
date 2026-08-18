FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY config-server/.mvn/ .mvn
COPY config-server/mvnw config-server/pom.xml ./
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
COPY config-server/src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY config-repo /config-repo
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]

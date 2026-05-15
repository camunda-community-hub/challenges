FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests && ls -lh target/

# docker build -t pierre-yves-monnet/automatetest:1.5.0 .
FROM eclipse-temurin:21-jdk-alpine
EXPOSE 9081
COPY --from=builder /app/target/automatetest-1.0.0-exec.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]


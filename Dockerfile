#
# Package stage
#
FROM openjdk:21-jdk-slim
ARG JAR_FILE
COPY accounting-app/target/accounting-app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY build/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
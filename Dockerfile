FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Добавляем curl для healthcheck
RUN apk add --no-cache curl

# Копируем собранный JAR из git
COPY build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
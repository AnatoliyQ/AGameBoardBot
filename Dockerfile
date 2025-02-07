FROM eclipse-temurin:17-jdk as build
WORKDIR /app

# Копируем только gradle файлы сначала
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .

# Делаем gradlew исполняемым
RUN chmod +x ./gradlew

# Загружаем зависимости
RUN ./gradlew dependencies

# Копируем исходный код
COPY src src

# Собираем приложение
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"] 
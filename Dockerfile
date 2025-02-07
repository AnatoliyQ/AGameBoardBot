FROM eclipse-temurin:17-jdk as build
WORKDIR /app

# Создаем базовую структуру Gradle проекта
COPY build.gradle settings.gradle ./
COPY src src/

# Создаем директорию для wrapper
RUN mkdir -p gradle/wrapper

# Создаем gradle-wrapper.properties
RUN echo "distributionBase=GRADLE_USER_HOME\n\
distributionPath=wrapper/dists\n\
distributionUrl=https\\://services.gradle.org/distributions/gradle-8.5-bin.zip\n\
networkTimeout=10000\n\
zipStoreBase=GRADLE_USER_HOME\n\
zipStorePath=wrapper/dists" > gradle/wrapper/gradle-wrapper.properties

# Скачиваем gradle-wrapper.jar
RUN curl -L -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar

# Создаем gradlew скрипты
RUN curl -L -o gradlew https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradlew && \
    curl -L -o gradlew.bat https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradlew.bat && \
    chmod +x gradlew

# Устанавливаем curl для скачивания файлов
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Загружаем зависимости
RUN ./gradlew dependencies --no-daemon

# Собираем приложение
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"] 
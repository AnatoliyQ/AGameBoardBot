FROM eclipse-temurin:17-jdk as build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"] 
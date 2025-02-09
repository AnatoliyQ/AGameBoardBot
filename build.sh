#!/bin/bash

echo "Building backend..."

# Проверяем наличие gradle wrapper
if [ ! -f "./gradlew" ]; then
    echo "Gradle wrapper not found. Creating..."
    gradle wrapper
fi

# Даем права на выполнение
chmod +x ./gradlew

# Очистка предыдущей сборки
./gradlew clean

# Сборка проекта
./gradlew bootJar

# Проверка успешности сборки
if [ $? -eq 0 ]; then
    echo "Build successful!"
    
    # Добавляем собранные файлы в git
    git add build/libs/*.jar -f
    git add src/main/resources/static/* -f
    
    echo "Now you can commit the changes with:"
    echo "git commit -m 'Update production build'"
else
    echo "Build failed!"
    exit 1
fi 
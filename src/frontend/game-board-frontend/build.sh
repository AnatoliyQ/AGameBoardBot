#!/bin/bash

# Установка зависимостей
npm install

# Сборка проекта
npm run build

# Проверка успешности сборки
if [ $? -eq 0 ]; then
    echo "Build successful!"
    
    # Копируем собранные файлы в ресурсы бэкенда
    cp -r dist/* ../../main/resources/static/
    
    echo "Frontend files copied to backend resources"
    echo "Now you can build backend and commit changes"
else
    echo "Build failed!"
    exit 1
fi 
#!/bin/bash

# Цвета для вывода
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Функция для вывода статуса
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Функция очистки при ошибке
cleanup_on_error() {
    print_warning "Произошла ошибка, очищаем ресурсы..."
    # Останавливаем все контейнеры
    docker-compose down --remove-orphans
    # Убиваем все процессы
    pkill -f "java.*board-games" || true
    pkill -f "ngrok" || true
    pkill -f "gradle" || true
    # Останавливаем ngrok
    if [ ! -z "$NGROK_PID" ]; then
        kill $NGROK_PID
    fi
    # Останавливаем приложение
    if [ ! -z "$APP_PID" ]; then
        kill $APP_PID
    fi
    # Останавливаем Docker контейнеры
    docker-compose down
    exit 1
}

# Устанавливаем обработчик ошибок
trap cleanup_on_error ERR

# Проверка наличия необходимых инструментов
if ! command -v docker &> /dev/null; then
    print_warning "Docker не установлен. Установите Docker Desktop для MacOS"
    print_warning "Выполните: brew install --cask docker"
    exit 1
fi

if ! command -v ngrok &> /dev/null; then
    print_warning "ngrok не установлен. Установите через brew:"
    print_warning "brew install jq ngrok"
    exit 1
fi

# Проверяем, запущен ли Docker демон
if ! docker info &> /dev/null; then
    print_warning "Docker демон не запущен. Запустите Docker Desktop"
    exit 1
fi

# Проверяем переменные окружения
if [ -z "$BOT_TOKEN" ]; then
    print_warning "BOT_TOKEN не установлен"
    exit 1
fi

if [ -z "$BOT_USERNAME" ]; then
    print_warning "BOT_USERNAME не установлен"
    exit 1
fi

# В начале скрипта добавляем полную очистку
print_status "Очистка окружения..."
# Останавливаем все контейнеры
docker-compose down --remove-orphans
# Убиваем все процессы
pkill -f "java.*board-games" || true
pkill -f "ngrok" || true
pkill -f "gradle" || true
sleep 5  # Даем время на завершение процессов

# Проверяем порты
for port in 8080 4040; do
    if lsof -i:$port > /dev/null; then
        print_warning "Порт $port занят. Пытаемся освободить..."
        lsof -ti:$port | xargs kill -9 || true
        sleep 2
    fi
done

# Дополнительная проверка
if lsof -i:8080 > /dev/null; then
    print_warning "Не удалось освободить порт 8080. Проверьте процессы вручную:"
    lsof -i:8080
    exit 1
fi

# Останавливаем существующие контейнеры и удаляем volumes
print_status "Очистка окружения..."
docker-compose down -v &> /dev/null
VOLUMES=$(docker volume ls -q | grep board-games || true)
if [ ! -z "$VOLUMES" ]; then
    docker volume rm $VOLUMES
fi

# Останавливаем все Java процессы, связанные с нашим приложением
pkill -f "java.*board-games" || true
sleep 2

# Запускаем базу данных и pgAdmin
print_status "Запускаем PostgreSQL и pgAdmin..."
docker-compose up -d

# Ждем, пока PostgreSQL запустится
sleep 5

print_status "Запускаем приложение..."
./gradlew bootRun --args='--spring.profiles.active=local' &
APP_PID=$!

# Ждем, пока приложение запустится
print_status "Ожидаем запуска приложения..."
sleep 20

print_status "Проверяем доступность приложения..."
curl -v http://localhost:8080/actuator/health || true

# Запускаем ngrok
print_status "Запускаем ngrok..."
# Убиваем существующие процессы ngrok
pkill -f ngrok || true

# Запускаем ngrok с явным указанием порта и конфигурации
ngrok http 8080 \
    --log=stdout \
    --log-level=debug \
    --region=eu > ngrok.log &
NGROK_PID=$!

# Ждем, пока ngrok запустится и создаст туннель
print_status "Ожидаем запуска ngrok..."
max_attempts=30
attempt=0
while [ $attempt -lt $max_attempts ]; do
    sleep 2
    attempt=$((attempt + 1))
    
    # Проверяем, запущен ли процесс ngrok
    if ! ps -p $NGROK_PID > /dev/null; then
        print_warning "ngrok процесс завершился"
        cat ngrok.log
        cleanup_on_error
    fi
    
    # Проверяем API ngrok
    TUNNELS_INFO=$(curl -s http://localhost:4040/api/tunnels || true)
    if [[ $TUNNELS_INFO == *"url"* ]]; then
        NGROK_URL=$(echo $TUNNELS_INFO | jq -r '.tunnels[] | select(.proto=="https") | .public_url')
        if [ ! -z "$NGROK_URL" ]; then
            print_status "ngrok запущен успешно: $NGROK_URL"
            break
        fi
    fi
    
    print_status "Ожидание ngrok (попытка $attempt из $max_attempts)..."
done

if [ $attempt -eq $max_attempts ]; then
    print_warning "ngrok не смог создать туннель"
    cat ngrok.log
    cleanup_on_error
fi

# Проверяем доступность туннеля
print_status "Проверяем доступность ngrok туннеля..."
curl -v "$NGROK_URL" || {
    print_warning "Не удалось подключиться к ngrok туннелю"
    cat ngrok.log
    cleanup_on_error
}

# Настраиваем webhook с новым URL
print_status "Настраиваем Webhook для бота..."
WEBHOOK_URL="${NGROK_URL}/callback/webhook"
WEBHOOK_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d "{
        \"url\": \"${WEBHOOK_URL}\",
        \"drop_pending_updates\": true,
        \"allowed_updates\": [\"message\", \"callback_query\"]
    }" \
    "https://api.telegram.org/bot${BOT_TOKEN}/setWebhook")

# Проверяем ответ webhook
if [[ $WEBHOOK_RESPONSE != *"\"ok\":true"* ]]; then
    print_warning "Ошибка настройки webhook: $WEBHOOK_RESPONSE"
    cleanup_on_error
fi

print_status "Webhook настроен успешно"

# Настраиваем Menu Button
print_status "Настраиваем Menu Button для бота..."
MENU_BUTTON_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d "{
        \"menu_button\": {
            \"type\": \"web_app\",
            \"text\": \"Настольные игры\",
            \"web_app\": {
                \"url\": \"${NGROK_URL}\"
            }
        }
    }" \
    "https://api.telegram.org/bot${BOT_TOKEN}/setChatMenuButton")

# Проверяем ответ menu button
if [[ $MENU_BUTTON_RESPONSE != *"\"ok\":true"* ]]; then
    print_warning "Ошибка настройки menu button: $MENU_BUTTON_RESPONSE"
    cleanup_on_error
fi

print_status "Menu Button настроен успешно"

# Проверяем статус webhook и меню
sleep 2
print_status "Проверяем настройки..."
WEBHOOK_INFO=$(curl -s "https://api.telegram.org/bot${BOT_TOKEN}/getWebhookInfo")
print_status "Webhook info: $WEBHOOK_INFO"

MENU_INFO=$(curl -s "https://api.telegram.org/bot${BOT_TOKEN}/getChatMenuButton")
print_status "Menu info: $MENU_INFO"

# Экспортируем переменные окружения
export WEBHOOK_URL=$NGROK_URL
export WEBAPP_URL=$NGROK_URL

print_status "Сервисы запущены успешно!"
print_status "PostgreSQL доступен на localhost:5432"
print_status "pgAdmin доступен на http://localhost:5050"
print_status ""
print_status "Учетные данные для PostgreSQL:"
print_status "Database: board_games_db"
print_status "Username: boardgames"
print_status "Password: boardgames"
print_status ""
print_status "Учетные данные для pgAdmin:"
print_status "URL: http://localhost:5050"
print_status "Email: admin@admin.com"
print_status "Password: admin"
print_status ""
print_status "Webhook URL: $WEBHOOK_URL"
print_status "WebApp URL: $WEBAPP_URL"

# Функция очистки при завершении
cleanup() {
    print_status "Завершение работы..."
    # Удаляем webhook
    curl -s "https://api.telegram.org/bot${BOT_TOKEN}/deleteWebhook"
    # Останавливаем ngrok
    kill $NGROK_PID
    rm ngrok.log
    # Останавливаем приложение
    kill $APP_PID
    # Останавливаем docker-compose
    docker-compose down
    exit 0
}

# Устанавливаем обработчик для корректного завершения
trap cleanup SIGINT SIGTERM

# Ожидаем завершения работы приложения
wait $APP_PID
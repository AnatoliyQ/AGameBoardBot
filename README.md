# AGameBoardBot
Telegram web app bot

# Game Board Application

## Локальная разработка

### База данных

```bash
# Запуск базы данных для разработки
docker-compose -f docker-compose-dev.yml up -d
```

### Бэкенд

```bash
# Запуск в режиме разработки
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Фронтенд

```bash
cd src/frontend/game-board-frontend

# Сборка
./build.sh

# Запуск в режиме разработки
npm run dev
```

## Сборка для Production

1. Собрать фронтенд:
```bash
cd src/frontend/game-board-frontend
./build.sh
```

2. Собрать бэкенд:
```bash
./build.sh
```

3. Закоммитить изменения:
```bash
git commit -m "Update production build"
git push
```

## Развертывание на сервере

```bash
git pull
docker-compose up -d
```

## Структура проекта

- `/src/main` - исходники бэкенда
- `/src/frontend/game-board-frontend` - исходники фронтенда
- `/src/main/resources/static` - собранный фронтенд
- `build/libs/*.jar` - собранный бэкенд

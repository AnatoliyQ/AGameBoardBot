# Этап сборки
FROM node:18-alpine as build

WORKDIR /app

# Устанавливаем переменные окружения для npm
ENV NODE_ENV=development
ENV NPM_CONFIG_LOGLEVEL=info
ENV NPM_CONFIG_CACHE=/tmp/.npm

# Копируем файлы для установки зависимостей
COPY package*.json ./

# Устанавливаем зависимости
RUN npm install

# Копируем исходники и конфигурационные файлы
COPY . .

# Проверяем окружение и файлы
RUN ls -la && \
    echo "Node version:" && node -v && \
    echo "NPM version:" && npm -v && \
    echo "Vite config:" && cat vite.config.js && \
    echo "Node modules:" && ls -la node_modules/@vitejs && \
    echo "Package.json:" && cat package.json && \
    echo "Bin directory:" && ls -la node_modules/.bin

# Собираем приложение используя полный путь
RUN ./node_modules/.bin/vite build

# Этап production
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Проверяем, что файлы скопированы
RUN ls -la /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"] 
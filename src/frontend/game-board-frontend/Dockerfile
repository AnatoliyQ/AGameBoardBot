# Этап сборки
FROM node:18-alpine as build

WORKDIR /app

# Копируем только необходимые файлы
COPY package*.json ./

# Сначала удаляем package-lock.json и устанавливаем зависимости заново
RUN rm -f package-lock.json && \
    npm install && \
    npm install -D vite@4.5.1 @vitejs/plugin-vue@4.5.2

# Копируем исходники
COPY . .

# Собираем приложение
RUN npm run build

# Этап production
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Проверяем, что файлы скопированы
RUN ls -la /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"] 
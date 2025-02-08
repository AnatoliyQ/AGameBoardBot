FROM node:18-alpine as build

RUN apk add --no-cache python3 make g++

WORKDIR /app
COPY package*.json ./
RUN npm cache clean --force && \
    npm ci && \
    npm install -g vite@4.5.1
COPY . .
RUN npm list
RUN npm run build
RUN ls -la dist/

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"] 
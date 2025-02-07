FROM node:18-alpine as build

RUN apk add --no-cache python3 make g++

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm install -g @vue/cli @vue/cli-service
RUN npm run build
RUN ls -la dist/ 
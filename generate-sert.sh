#!/bin/bash

# Создаем директорию для сертификатов
mkdir -p src/main/resources/keystore

# Генерируем самоподписанный сертификат
keytool -genkeypair \
  -alias local-cert \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore src/main/resources/keystore/local-cert.p12 \
  -validity 365 \
  -dname "CN=localhost, OU=Development, O=Your Organization, L=Your City, ST=Your State, C=Your Country" \
  -storepass changeit \
  -keypass changeit
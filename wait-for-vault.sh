#!/bin/sh

VAULT_ADDR='http://vault_ent:8200'

echo "Waiting for Vault to be fully configured..."

# Ждем, пока role_id.txt и secret_id.txt будут заполнены
while [ ! -s /role_id.txt ] || [ ! -s /secret_id.txt ]; do
  echo "Waiting for role_id.txt and secret_id.txt to be generated..."
  sleep 2
done

echo "role_id.txt and secret_id.txt are ready."

# Чтение значений role_id и secret_id
ROLE_ID=$(cat /role_id.txt)
SECRET_ID=$(cat /secret_id.txt)

# Проверка, что значения не пустые
if [ -z "$ROLE_ID" ] || [ -z "$SECRET_ID" ]; then
  echo "Error: role_id or secret_id is empty."
  exit 1
fi

echo "role_id and secret_id have been successfully loaded."

# Экспорт значений в переменные окружения
export ROLE_ID
export SECRET_ID

echo "Starting the application..."

# Запуск приложения
exec java $JAVA_OPTS -jar /app/app.jar

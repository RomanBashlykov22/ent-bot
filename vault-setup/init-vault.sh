#!/bin/sh

vault server -dev -dev-root-token-id="root" -dev-listen-address="0.0.0.0:8200" &
export VAULT_ADDR='http://vault_ent:8200'
export VAULT_TOKEN='root'

sleep 5

if [ -z "$TG_BOT_NAME" ] || [ -z "$TG_BOT_TOKEN" ] || [ -z "$OPEN_AI_KEY" ] || [ -z "$OPEN_AI_ASSISTANT_ID" ]; then
  echo "Error: One or more required environment variables are missing:"
  echo "TG_BOT_NAME=$TG_BOT_NAME"
  echo "TG_BOT_TOKEN=$TG_BOT_TOKEN"
  echo "OPEN_AI_KEY=$OPEN_AI_KEY"
  echo "OPEN_AI_ASSISTANT_ID=$OPEN_AI_ASSISTANT_ID"
  exit 1
fi

echo "Configuring Vault..."

# Enable KV secrets engine
vault secrets enable -path=kv kv-v2 || echo "KV already enabled"
echo "KV Enabled"

# Write secrets to Vault (values will be replaced from .env)
vault kv put kv/ent-bot \
  tg.bot.name="$TG_BOT_NAME" \
  tg.bot.token="$TG_BOT_TOKEN" \
  open-ai.key="$OPEN_AI_KEY" \
  open-ai.assistant.id="$OPEN_AI_ASSISTANT_ID"
echo "Secrets put to KV"

# Create policy
vault policy write ent-bot-policy - <<EOF
path "kv/data/ent-bot" {
  capabilities = ["read", "list"]
}
path "kv/metadata/ent-bot" {
  capabilities = ["list"]
}
path "sys/renew/*" {
  capabilities = ["update"]
}
EOF
echo "Policy created"

# Enable AppRole authentication
vault auth enable approle
echo "APPROLE enabled"

# Create role for application
vault write auth/approle/role/ent-bot-role \
  policies="ent-bot-policy" \
  token_ttl=24h secret_id_ttl=0
echo "Role created"

# Create role for application
ROLE_ID=$(vault read -field=role_id auth/approle/role/ent-bot-role/role-id)
SECRET_ID=$(vault write -f -field=secret_id auth/approle/role/ent-bot-role/secret-id)

# Записываем только значения
echo "$ROLE_ID" > /role_id.txt
echo "$SECRET_ID" > /secret_id.txt


echo "Vault is initialized and configured."

tail -f /dev/null
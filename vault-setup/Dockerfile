# Using HashiCorp Vault image
FROM hashicorp/vault:1.15

# Install curl
RUN apk add --no-cache curl

# Copy initialization script
COPY init-vault.sh /init-vault.sh

# Make it executable
RUN chmod +x /init-vault.sh

# Run the initialization script
ENTRYPOINT ["/init-vault.sh"]

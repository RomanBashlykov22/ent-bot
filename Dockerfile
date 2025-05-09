# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

RUN apt update && apt install -y curl netcat-openbsd

# Copy the project files
COPY target/*.jar app.jar
# Wait for Vault initialization
COPY wait-for-vault.sh /wait-for-vault.sh

RUN chmod +x /wait-for-vault.sh

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["/wait-for-vault.sh"]

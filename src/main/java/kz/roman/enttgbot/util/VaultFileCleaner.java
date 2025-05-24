package kz.roman.enttgbot.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Component
public class VaultFileCleaner {

    private static final Logger logger = Logger.getLogger(VaultFileCleaner.class.getName());
    private static final String DEFAULT_VAULT_PATH = "vault-setup";

    @PostConstruct
    public void clearVaultFiles() {
        try {
            // Используем путь из переменной окружения или по умолчанию
            String vaultPath = System.getenv().getOrDefault("VAULT_FILES_PATH", DEFAULT_VAULT_PATH);
            Path roleIdPath = Paths.get(vaultPath, "role_id");
            Path secretIdPath = Paths.get(vaultPath, "secret_id");

            // Удаляем role_id
            if (Files.exists(roleIdPath) && Files.size(roleIdPath) > 0) {
                Files.delete(roleIdPath);
                logger.info("🧹 Очищен файл role_id");
            } else if (Files.exists(roleIdPath) && Files.size(roleIdPath) == 0) {
                logger.warning("⚠️ Файл role_id пуст. Возможно, данные не были загружены.");
            }

            // Удаляем secret_id
            if (Files.exists(secretIdPath) && Files.size(secretIdPath) > 0) {
                Files.delete(secretIdPath);
                logger.info("🧹 Очищен файл secret_id");
            } else if (Files.exists(secretIdPath) && Files.size(secretIdPath) == 0) {
                logger.warning("⚠️ Файл secret_id пуст. Возможно, данные не были загружены.");
            }

        } catch (IOException e) {
            logger.severe("❌ Ошибка при очистке файлов Vault: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package kz.roman.enttgbot.config;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.AssistantsClientBuilder;
import com.azure.ai.openai.assistants.models.Assistant;
import com.azure.core.credential.KeyCredential;
import kz.roman.enttgbot.bot.EntBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class AppConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(EntBot entBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(entBot);
        return api;
    }

    @Bean
    public AssistantsClient assistantsClient(@Value("${open-ai.key}") String key) {
        return new AssistantsClientBuilder()
                .credential(new KeyCredential(key))
                .buildClient();
    }

    @Bean
    public Assistant assistant(AssistantsClient assistantsClient, @Value("${open-ai.assistant.id}") String id) {
        return assistantsClient.getAssistant(id);
    }
}

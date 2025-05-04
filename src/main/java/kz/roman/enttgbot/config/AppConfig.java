package kz.roman.enttgbot.config;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.AssistantsClientBuilder;
import com.azure.ai.openai.assistants.models.Assistant;
import com.azure.core.credential.KeyCredential;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kz.roman.enttgbot.bot.EntBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.vector.DoubleVector;
import smile.regression.LinearModel;
import smile.regression.OLS;

import java.time.Duration;

@Configuration
@EnableCaching
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

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Поддержка дат
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);  // Включаем сохранение типа в JSON

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(1))
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> cacheName + ":") // Меняем префикс ключей
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Настройка сериализации
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY
        );

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public double[][] xData() {
        return new double[][]{
                {1, 1, 30}, {3, 0, 120}, {2, 1, 60},
                {1, 1, 15}, {1, 0, 15}, {1, 1, 45},
                {1, 1, 60}, {1, 1, 75}, {1, 1, 120},
                {2, 1, 45}, {2, 1, 30}, {2, 1, 80},
                {2, 1, 100}, {2, 1, 120}, {2, 0, 120},
                {2, 0, 15}, {3, 0, 15}, {3, 1, 15},
                {3, 1, 30}, {3, 1, 45}, {3, 1, 60},
                {3, 1, 80}, {3, 1, 100}, {3, 1, 120},
                {3, 1, 150}, {3, 1, 200}, {1, 1, 15},
                {1, 1, 20}, {1, 0, 30}, {1, 1, 50},
                {1, 1, 90}, {1, 0, 10}, {2, 1, 25},
                {2, 1, 70}, {2, 0, 90}, {2, 1, 110},
                {2, 0, 20}, {3, 1, 20}, {3, 1, 40},
                {3, 0, 180}, {3, 1, 130}, {3, 1, 170},
                {3, 0, 30}, {1, 1, 10}, {1, 0, 25},
                {1, 1, 35}, {1, 1, 55}, {1, 1, 85},
                {1, 0, 40}, {1, 1, 100}, {2, 1, 20},
                {2, 0, 50}, {2, 1, 40}, {2, 1, 65},
                {2, 1, 95}, {2, 0, 70}, {2, 1, 130},
                {3, 1, 18}, {3, 0, 25}, {3, 1, 35},
                {3, 1, 70}, {3, 1, 110}, {3, 0, 60},
                {3, 1, 180}, {1, 1, 5}, {1, 1, 25},
                {1, 1, 40}, {1, 1, 65}, {1, 1, 95},
                {1, 1, 130}, {1, 0, 5}, {1, 0, 40},
                {1, 0, 120}, {2, 1, 10}, {2, 1, 35},
                {2, 1, 55}, {2, 1, 85}, {2, 1, 115},
                {2, 1, 160}, {2, 0, 15}, {2, 0, 75},
                {2, 0, 150}, {3, 1, 12}, {3, 1, 28},
                {3, 1, 50}, {3, 1, 90}, {3, 1, 140},
                {3, 1, 220}, {3, 0, 20}, {3, 0, 80},
                {3, 0, 190}, {1, 1, 0}, {2, 1, 300},
                {3, 1, 2}
        };
    }

    @Bean
    public double[] yData() {
        return new double[]{
                0.9, 0, 0.6,
                1.0, 0, 0.6, 0.4, 0.2, 0.1,
                0.8, 1.0, 0.4, 0.2, 0.1, 0, 0,
                0, 1.0, 1.0, 1.0, 1.0, 0.8,
                0.6, 0.5, 0.3, 0.1, 1.0,
                0.95, 0, 0.4, 0.15, 0,
                1.0, 0.35, 0, 0.15, 0,
                1.0, 1.0, 0, 0.4, 0.2, 0,
                1.0, 0, 0.9, 0.75, 0.25, 0, 0.1,
                1.0, 0, 0.9, 0.7, 0.3, 0, 0.1,
                1.0, 0, 1.0, 0.85, 0.6, 0, 0.2,
                1.0, 1.0, 0.85, 0.75, 0.4, 0.05, 0, 0, 0,
                1.0, 1.0, 0.85, 0.55, 0.35, 0.15, 0, 0, 0,
                1.0, 1.0, 1.0, 0.85, 0.55, 0.1, 0, 0, 0,
                1.0, 0.05, 1.0
        };
    }

    @Bean
    public LinearModel ols(double[][] xData, double[] yData) {
        double[] difficulty = new double[xData.length];
        double[] correct = new double[xData.length];
        double[] time = new double[xData.length];
        double[] score = new double[xData.length];

        for (int i = 0; i < xData.length; i++) {
            difficulty[i] = xData[i][0];
            correct[i] = xData[i][1];
            time[i] = xData[i][2];
            score[i] = yData[i];
        }

        DataFrame df = DataFrame.of(
                DoubleVector.of("difficulty", difficulty),
                DoubleVector.of("correct", correct),
                DoubleVector.of("time", time),
                DoubleVector.of("score", score)
        );

        return OLS.fit(Formula.lhs("score"), df);
    }
}

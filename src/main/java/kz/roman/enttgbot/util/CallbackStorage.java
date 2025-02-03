package kz.roman.enttgbot.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CallbackStorage {
    private static final int EXPIRATION_TIME_MINUTES = 30;
    private static final Map<String, String> callbackMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private CallbackStorage() {
        throw new IllegalStateException("Utility class");
    }

    public static String store(String data) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        callbackMap.put(uniqueId, data);

        scheduler.schedule(() -> callbackMap.remove(uniqueId), EXPIRATION_TIME_MINUTES, TimeUnit.MINUTES);

        return uniqueId;
    }

    public static String retrieve(String id) {
        return callbackMap.remove(id);
    }
}
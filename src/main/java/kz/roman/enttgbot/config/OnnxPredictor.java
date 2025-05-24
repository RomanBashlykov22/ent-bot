package kz.roman.enttgbot.config;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Component
public class OnnxPredictor {
    private final OrtEnvironment env;
    private final OrtSession session;

    public OnnxPredictor(@Value("${model.name}") String resourceName) throws Exception {
        env = OrtEnvironment.getEnvironment();
        System.out.println("🔍 Попытка загрузки модели: " + resourceName);

        // 1) Открываем ресурс из classpath
        try (InputStream modelStream =
                     getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (modelStream == null) {
                throw new IllegalArgumentException(
                        "Resource not found: " + resourceName);
            }
            // 2) Копируем во временный файл, т.к. createSession(Path) ожидает файл на диске
            Path tempFile = Files.createTempFile("model-", ".onnx");
            Files.copy(modelStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            // 3) Создаём сессию из пути к временной копии
            session = env.createSession(tempFile.toString(), new OrtSession.SessionOptions());
        }
    }

    public float predict(float[] inputData) throws OrtException {
        try (OnnxTensor inputTensor = OnnxTensor.createTensor(
                env, FloatBuffer.wrap(inputData), new long[]{1, inputData.length})) {

            // Имя входа должно совпадать с тем, что внутри вашей модели
            Map<String, OnnxTensor> inputs = Map.of("float_input", inputTensor);

            try (OrtSession.Result res = session.run(inputs)) {
                float[][] out = (float[][]) res.get(0).getValue();
                return out[0][0];
            }
        }
    }
}

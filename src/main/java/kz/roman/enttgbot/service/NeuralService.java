package kz.roman.enttgbot.service;

import kz.roman.enttgbot.model.dto.GradationScore;
import kz.roman.enttgbot.model.dto.TestSession;
import kz.roman.enttgbot.model.dto.UserAnswer;
import kz.roman.enttgbot.model.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smile.regression.LinearModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NeuralService {

    private final LinearModel linearModel;

    public double[][] buildAnswerMatrix(TestSession testSession, List<UserAnswer> userAnswers) {
        List<List<Double>> result = new ArrayList<>();
        List<Question> questions = testSession.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            UserAnswer userAnswer = userAnswers.get(i);

            // 1. Сложность (переводим строку в число)
            double difficulty = mapDifficulty(question.getDifficulty().toString());

            // 2. Верность ответа
            double isCorrect = question.getCorrectOption().equals(userAnswer.getSelectedOption()) ? 1.0 : 0.0;

            // 3. Потраченное время
            double timeSpentSeconds = testSession.getQuestionSentTimestamps().get(i);

            List<Double> answerData = List.of(difficulty, isCorrect, timeSpentSeconds);
            result.add(answerData);
        }

        return convertToPrimitiveArray(result);
    }

    private double mapDifficulty(String difficulty) {
        if (difficulty == null) {
            return 1.0; // по умолчанию "easy"
        }
        return switch (difficulty.toLowerCase()) {
            case "easy" -> 1.0;
            case "medium" -> 2.0;
            case "difficult" -> 3.0;
            default -> 1.0; // если вдруг что-то не то, считаем "easy"
        };
    }

    private double[][] convertToPrimitiveArray(List<List<Double>> data) {
        double[][] array = new double[data.size()][3];
        for (int i = 0; i < data.size(); i++) {
            List<Double> row = data.get(i);
            for (int j = 0; j < 3; j++) {
                array[i][j] = row.get(j);
            }
        }
        return array;
    }

    public GradationScore gradation(double[][] data) {
        GradationScore gradationScore = new GradationScore();
        for (double[] sample : data) {
            double prediction = linearModel.predict(sample);
            if (prediction < 0) prediction = 0;
            if (prediction > 1) prediction = 1;
            System.out.printf("Input: %s → Predicted score: %.4f%n",
                    java.util.Arrays.toString(sample), prediction);
            gradationScore.getScores().add(prediction);
        }
        gradationScore.setTotalScore(gradationScore.getScores().stream().mapToDouble(Double::doubleValue).average().orElse(0));
        System.out.println("total " + gradationScore.getTotalScore());
        return gradationScore;
    }
}

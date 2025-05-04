package kz.roman.enttgbot.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AIRequest {
    private List<UserAnswer> userAnswers = new ArrayList<>();
    private String totalScore;
}

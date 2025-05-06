package kz.roman.enttgbot.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private Long topicId;
    private Long subtopicId;
    private String questionText;
    private List<String> options;
    private String correctOption;
    private String difficulty;
}

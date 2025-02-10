package kz.roman.enttgbot.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAnswer {
    private String question;
    private List<String> options;
    private String correctOption;
    private String selectedOption;
}

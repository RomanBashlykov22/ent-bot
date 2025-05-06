package kz.roman.enttgbot.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiUserAnswers {
    private String question;
    private List<String> options;
    private String correctOption;
    private String selectedOption;
    private Double seconds;
}

package kz.roman.enttgbot.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GradationScore {
    private List<Double> scores = new ArrayList<>();
    private Double totalScore;
}

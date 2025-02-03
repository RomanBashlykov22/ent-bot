package kz.roman.enttgbot.model.dto;

import kz.roman.enttgbot.model.entity.Question;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class TestSession {
    private final List<Question> questions;
    private final Map<Integer, String> answers = new HashMap<>();

    public TestSession(List<Question> questions) {
        this.questions = questions;
    }
}

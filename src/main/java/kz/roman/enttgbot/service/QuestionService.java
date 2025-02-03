package kz.roman.enttgbot.service;

import kz.roman.enttgbot.model.entity.Question;
import kz.roman.enttgbot.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getQuestionsByTopic(long topicId) {
        return questionRepository.findAllByTopicId(topicId);
    }

    public List<Question> getQuestionsBySubtopic(long subtopicId) {
        return questionRepository.findAllBySubtopicId(subtopicId);
    }
}

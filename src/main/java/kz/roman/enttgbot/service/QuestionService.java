package kz.roman.enttgbot.service;

import jakarta.persistence.EntityNotFoundException;
import kz.roman.enttgbot.model.dto.QuestionDto;
import kz.roman.enttgbot.model.entity.Question;
import kz.roman.enttgbot.model.entity.Subtopic;
import kz.roman.enttgbot.model.entity.Topic;
import kz.roman.enttgbot.repository.QuestionRepository;
import kz.roman.enttgbot.repository.SubtopicRepository;
import kz.roman.enttgbot.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final SubtopicRepository subtopicRepository;

    public List<Question> getQuestionsByTopic(long topicId) {
        return questionRepository.findAllByTopicId(topicId);
    }

    public List<Question> getQuestionsBySubtopic(long subtopicId) {
        return questionRepository.findAllBySubtopicId(subtopicId);
    }

    public List<Question> getAll(Long topicId, Long subtopicId) {
        if(topicId == null && subtopicId == null)
            return questionRepository.findAll();
        else if(topicId != null && subtopicId == null)
            return getQuestionsByTopic(topicId);
        else
            return getQuestionsBySubtopic(subtopicId);
    }

    public Question create(QuestionDto dto) {
        Topic topic = topicRepository.findById(dto.getTopicId()).orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        Subtopic subtopic = subtopicRepository.findById(dto.getSubtopicId()).orElseThrow(() -> new EntityNotFoundException("Subtopic not found"));
        Question question = Question.builder()
                .topic(topic)
                .subtopic(subtopic)
                .questionText(dto.getQuestionText())
                .correctOption(dto.getCorrectOption())
                .options(dto.getOptions())
                .difficulty(Question.QuestionDifficulty.valueOf(dto.getDifficulty()))
                .build();
        return questionRepository.save(question);
    }

    public Question update(Long id, QuestionDto dto) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Question not found"));
        Topic topic = topicRepository.findById(dto.getTopicId()).orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        Subtopic subtopic = subtopicRepository.findById(dto.getSubtopicId()).orElseThrow(() -> new EntityNotFoundException("Subtopic not found"));
        question.setTopic(topic);
        question.setSubtopic(subtopic);
        question.setQuestionText(dto.getQuestionText());
        question.setCorrectOption(dto.getCorrectOption());
        question.setOptions(dto.getOptions());
        question.setDifficulty(Question.QuestionDifficulty.valueOf(dto.getDifficulty()));
        return questionRepository.save(question);
    }

    public void delete(Long id) {
        questionRepository.deleteById(id);
    }
}

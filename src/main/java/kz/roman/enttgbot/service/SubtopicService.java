package kz.roman.enttgbot.service;

import jakarta.persistence.EntityNotFoundException;
import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.model.dto.SubtopicDto;
import kz.roman.enttgbot.model.entity.Subtopic;
import kz.roman.enttgbot.model.entity.Topic;
import kz.roman.enttgbot.repository.SubtopicRepository;
import kz.roman.enttgbot.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubtopicService {
    private final SubtopicRepository subtopicRepository;
    private final TopicRepository topicRepository;

    public List<Subtopic> findByTopicId(long topicId) {
        return subtopicRepository.findAllByTopicId(topicId);
    }

    public List<Subtopic> findAll() {
        return subtopicRepository.findAll();
    }

    public Subtopic create(SubtopicDto dto) {
        Topic topic = topicRepository.findById(dto.getTopicId()).orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        Subtopic subtopic = Subtopic.builder()
                .name(dto.getName())
                .topic(topic)
                .build();
        return subtopicRepository.save(subtopic);
    }

    public Subtopic update(Long id, SubtopicDto dto) {
        Topic topic = topicRepository.findById(dto.getTopicId()).orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        Subtopic subtopic = subtopicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Subtopic not found"));
        subtopic.setName(dto.getName());
        subtopic.setTopic(topic);
        return subtopicRepository.save(subtopic);
    }

    public void delete(Long id) {
        subtopicRepository.deleteById(id);
    }
}

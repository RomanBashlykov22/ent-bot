package kz.roman.enttgbot.service;

import jakarta.persistence.EntityNotFoundException;
import kz.roman.enttgbot.model.dto.TopicDto;
import kz.roman.enttgbot.model.entity.Topic;
import kz.roman.enttgbot.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;

    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    public Topic create(TopicDto dto) {
        return topicRepository.save(Topic.builder().name(dto.getTopicName()).build());
    }

    public Topic update(Long id, TopicDto dto) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        topic.setName(dto.getTopicName());
        return topicRepository.save(topic);
    }

    public void delete(Long id) {
        topicRepository.deleteById(id);
    }
}

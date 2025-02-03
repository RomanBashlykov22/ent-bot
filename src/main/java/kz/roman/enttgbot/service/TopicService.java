package kz.roman.enttgbot.service;

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
}

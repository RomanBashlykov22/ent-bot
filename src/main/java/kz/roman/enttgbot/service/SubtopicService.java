package kz.roman.enttgbot.service;

import kz.roman.enttgbot.model.entity.Subtopic;
import kz.roman.enttgbot.repository.SubtopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubtopicService {
    private final SubtopicRepository subtopicRepository;

    public List<Subtopic> findByTopicId(long topicId) {
        return subtopicRepository.findAllByTopicId(topicId);
    }
}

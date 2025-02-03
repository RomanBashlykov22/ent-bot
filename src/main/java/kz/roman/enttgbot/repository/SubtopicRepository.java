package kz.roman.enttgbot.repository;

import kz.roman.enttgbot.model.entity.Subtopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubtopicRepository extends JpaRepository<Subtopic, Long> {
    List<Subtopic> findAllByTopicId(Long topicId);
}

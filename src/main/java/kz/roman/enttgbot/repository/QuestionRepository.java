package kz.roman.enttgbot.repository;

import kz.roman.enttgbot.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findAllByTopicId(Long topicId);

    List<Question> findAllBySubtopicId(Long subtopicId);
}

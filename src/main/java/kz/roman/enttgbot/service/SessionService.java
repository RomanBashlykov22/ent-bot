package kz.roman.enttgbot.service;

import jakarta.persistence.EntityNotFoundException;
import kz.roman.enttgbot.model.dto.ApiUserAnswers;
import kz.roman.enttgbot.model.dto.RecentErrors;
import kz.roman.enttgbot.model.entity.Question;
import kz.roman.enttgbot.model.entity.Session;
import kz.roman.enttgbot.model.entity.Topic;
import kz.roman.enttgbot.model.entity.User;
import kz.roman.enttgbot.repository.QuestionRepository;
import kz.roman.enttgbot.repository.SessionRepository;
import kz.roman.enttgbot.repository.TopicRepository;
import kz.roman.enttgbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(Session session) {
        sessionRepository.save(session);
    }

    public List<Session> getUserResults(String userId) {
        return sessionRepository.findAllByUserOrderBySessionDateDesc(new User(userId), PageRequest.of(0, 10));
    }

    public List<RecentErrors> getRecentError(String userId) {
        String key = "user:" + userId + ":mistakes";
        Map<Object, Object> mistakes = redisTemplate.opsForHash().entries(key);
        List<RecentErrors> recentErrors = new ArrayList<>();
        mistakes.forEach((topic, count) -> {
            if((int)count >= 5) {
                recentErrors.add(new RecentErrors((String)topic, (int)count));
            }
        });
        return recentErrors;
    }

    public Session processSession(String userId, List<ApiUserAnswers> userAnswers) {
        List<Question> questions = new ArrayList<>();

        userAnswers.forEach(q -> questions.add(questionRepository.findByQuestionText(q.getQuestion()).orElseThrow(() -> new EntityNotFoundException("Question not found"))));

        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            if(questions.get(i).getCorrectOption().equals(userAnswers.get(i).getSelectedOption()))
                correctAnswers++;
        }

        Session session = Session.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found")))
                .topic(questions.get(0).getTopic())
                .correctAnswers(correctAnswers)
                .totalQuestions(questions.size())
                .build();
        return sessionRepository.save(session);
    }
}

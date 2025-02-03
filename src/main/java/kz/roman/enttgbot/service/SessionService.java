package kz.roman.enttgbot.service;

import kz.roman.enttgbot.model.entity.Session;
import kz.roman.enttgbot.model.entity.User;
import kz.roman.enttgbot.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public void save(Session session) {
        sessionRepository.save(session);
    }

    public List<Session> getUserResults(String userId) {
        return sessionRepository.findAllByUserOrderBySessionDateDesc(new User(userId), PageRequest.of(0, 10));
    }
}

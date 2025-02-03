package kz.roman.enttgbot.service;

import kz.roman.enttgbot.model.entity.User;
import kz.roman.enttgbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void save(String chatId) {
        userRepository.save(new User(chatId));
    }
}

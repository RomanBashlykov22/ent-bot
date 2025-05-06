package kz.roman.enttgbot.controller;

import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestParam String chatId) {
        userService.save(chatId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Пользователь зарегистрирован"));
    }
}

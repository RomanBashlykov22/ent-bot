package kz.roman.enttgbot.controller;

import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.model.dto.AIRequest;
import kz.roman.enttgbot.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AIService aiService;

    @PostMapping("/check")
    public ResponseEntity<ApiResponse<String>> checkUserAnswers(@RequestBody AIRequest aiRequest) throws InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(aiService.checkAnswers(aiRequest)));
    }

    @PostMapping("/conversation")
    public ResponseEntity<ApiResponse<String>> conversation(@RequestBody String question) throws InterruptedException {
        return ResponseEntity.ok(ApiResponse.success(aiService.askAi(question)));
    }
}

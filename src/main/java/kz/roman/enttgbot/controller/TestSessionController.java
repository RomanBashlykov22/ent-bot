package kz.roman.enttgbot.controller;

import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.model.dto.ApiUserAnswers;
import kz.roman.enttgbot.model.dto.RecentErrors;
import kz.roman.enttgbot.model.entity.Session;
import kz.roman.enttgbot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests/sessions")
@RequiredArgsConstructor
public class TestSessionController {
    private final SessionService sessionService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Session>> submitSession(@PathVariable String userId,
                                                              @RequestBody List<ApiUserAnswers> userAnswers) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(sessionService.processSession(userId, userAnswers)));
    }

    @GetMapping("/{userId}/recent-errors")
    public ResponseEntity<ApiResponse<RecentErrors>> getRecentErrors(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(sessionService.getRecentError(userId)));
    }
}

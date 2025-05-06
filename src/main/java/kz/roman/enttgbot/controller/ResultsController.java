package kz.roman.enttgbot.controller;

import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.model.entity.Session;
import kz.roman.enttgbot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultsController {
    private final SessionService sessionService;

    @GetMapping("/{userId}/results")
    public ResponseEntity<ApiResponse<Session>> getUserResults(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(sessionService.getUserResults(userId)));
    }
}

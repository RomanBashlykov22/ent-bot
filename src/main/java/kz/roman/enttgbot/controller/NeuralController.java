package kz.roman.enttgbot.controller;

import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.model.dto.AIRequest;
import kz.roman.enttgbot.model.dto.ApiUserAnswers;
import kz.roman.enttgbot.model.dto.GradationScore;
import kz.roman.enttgbot.service.NeuralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/neural")
@RequiredArgsConstructor
public class NeuralController {
    private final NeuralService neuralService;

    @PostMapping("/matrix")
    public ResponseEntity<ApiResponse<double[][]>> buildMatrix(@RequestBody List<ApiUserAnswers> userAnswers) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(neuralService.buildAnswerMatrix(userAnswers)));
    }

    @PostMapping("/gradation/score")
    public ResponseEntity<ApiResponse<GradationScore>> gradationScore(@RequestBody double[][] matrix) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(neuralService.gradation(matrix)));
    }
}

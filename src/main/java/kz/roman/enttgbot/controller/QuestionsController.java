package kz.roman.enttgbot.controller;

import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.model.dto.QuestionDto;
import kz.roman.enttgbot.model.entity.Question;
import kz.roman.enttgbot.model.entity.Subtopic;
import kz.roman.enttgbot.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests/questions")
@RequiredArgsConstructor
public class QuestionsController {
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Question>> getAll(@RequestParam(required = false) Long topicId,
                                    @RequestParam(required = false) Long subtopicId) {
        return ResponseEntity.ok(ApiResponse.success(questionService.getAll(topicId, subtopicId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Question>> create(@RequestBody QuestionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(questionService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> update(@PathVariable Long id, @RequestBody QuestionDto dto) {
        return ResponseEntity.ok(ApiResponse.success(questionService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

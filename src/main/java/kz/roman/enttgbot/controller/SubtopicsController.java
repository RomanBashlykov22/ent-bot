package kz.roman.enttgbot.controller;

import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.model.dto.SubtopicDto;
import kz.roman.enttgbot.model.entity.Subtopic;
import kz.roman.enttgbot.service.SubtopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class SubtopicsController {
    private final SubtopicService subtopicService;

    @GetMapping("/topics/{topicId}/subtopics")
    public ResponseEntity<ApiResponse<Subtopic>> getByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(ApiResponse.success(subtopicService.findByTopicId(topicId)));
    }

    @GetMapping("/subtopics")
    public ResponseEntity<ApiResponse<Subtopic>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(subtopicService.findAll()));
    }

    @PostMapping("/topics/subtopics")
    public ResponseEntity<ApiResponse<Subtopic>> create(@RequestBody SubtopicDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(subtopicService.create(dto)));
    }

    @PutMapping("/subtopics/{id}")
    public ResponseEntity<ApiResponse<Subtopic>> update(@PathVariable Long id, @RequestBody SubtopicDto dto) {
        return ResponseEntity.ok(ApiResponse.success(subtopicService.update(id, dto)));
    }

    @DeleteMapping("/subtopics/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        subtopicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

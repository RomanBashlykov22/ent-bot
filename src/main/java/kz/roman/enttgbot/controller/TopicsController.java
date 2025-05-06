package kz.roman.enttgbot.controller;

import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.model.dto.TopicDto;
import kz.roman.enttgbot.model.entity.Topic;
import kz.roman.enttgbot.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests/topics")
@RequiredArgsConstructor
public class TopicsController {
    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<ApiResponse<Topic>> getAllTopics() {
        return ResponseEntity.ok(ApiResponse.success(topicService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Topic>> createTopic(@RequestBody TopicDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(topicService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Topic>> updateTopic(@PathVariable Long id, @RequestBody TopicDto dto) {
        return ResponseEntity.ok(ApiResponse.success(topicService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTopic(@PathVariable Long id) {
        topicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

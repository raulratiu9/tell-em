package com.tellem.controller;

import com.tellem.model.Story;
import com.tellem.model.dto.StoryDto;
import com.tellem.service.StoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/stories")
public class StoryController {
    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    public ResponseEntity<Story> createStoryFromJson(@RequestBody StoryDto input) {
        Story story = storyService.createGraphFromInput(input);
        return ResponseEntity.ok(story);
    }

    @GetMapping
    public Flux<Story> getAllStories() {
        return storyService.getAllStories();
    }

    @GetMapping("/{title}")
    public Mono<Story> getStoryByTitle(@PathVariable String title) {
        return storyService.getStoryByTitle(title);
    }
}

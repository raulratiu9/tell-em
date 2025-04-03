package com.tellem.controller;

import com.tellem.model.Story;
import com.tellem.service.StoryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping
    public List<Story> getAllStories() {
        return storyService.getAllStories();
    }

    @GetMapping("/{title}")
    public Mono<Story> getStoryByTitle(@PathVariable String title) {
        return storyService.getStoryByTitle(title);
    }

    @PostMapping
    public Story saveStory(@RequestBody Story story) {
        return storyService.saveStory(story);
    }
}

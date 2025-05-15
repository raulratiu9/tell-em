package com.tellem.controller;

import com.tellem.model.Story;
import com.tellem.model.dto.StoryDto;
import com.tellem.service.StoryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/stories")
public class StoryController {
    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    public Story createStoryFromJson(@RequestBody StoryDto input) {
        return storyService.createGraphFromInput(input);
    }


    @GetMapping("/search")
    public Flux<Story> searchStories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String query
    ) {
        return storyService.searchStories(query, page, size);
    }

    @GetMapping("/all")
    public Flux<Story> getAllStories() {
        return storyService.getAllStories();
    }

    @GetMapping
    public Flux<Story> getPaginatedStories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        return storyService.getPaginatedStories(page, size);
    }

    @GetMapping("/{title}")
    public Mono<Story> getStoryByTitle(@PathVariable String title) {
        return storyService.getStoryByTitle(title);
    }
}

package com.tellem.controller;

import com.tellem.model.Story;
import com.tellem.model.dto.StoryDto;
import com.tellem.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    public ResponseEntity<String> createStory(@RequestBody StoryDto storyRequest) {
        if (storyRequest.getTitle().isEmpty() || storyRequest.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Story savedStory = storyService.saveStory(storyRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStory.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping
    public ResponseEntity<List<StoryDto>> getAllStories() {
        List<StoryDto> stories = storyService.getAllStories();
        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryDto> getStoryById(@PathVariable long id) {
        StoryDto story = storyService.getStoryById(id);
        return new ResponseEntity<>(story, HttpStatus.OK);
    }
}

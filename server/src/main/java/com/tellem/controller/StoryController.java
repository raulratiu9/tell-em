package com.tellem.controller;

import com.tellem.exception.InvalidStoryException;
import com.tellem.model.Story;
import com.tellem.model.dto.StoryDto;
import com.tellem.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tellem.utils.StoryValidationUtils.isNullOrEmpty;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    public ResponseEntity<?> createStory(@RequestBody StoryDto storyRequest) {
        if (isNullOrEmpty(storyRequest.getTitle()) || isNullOrEmpty(storyRequest.getDescription())) {
            return ResponseEntity.badRequest().body("Title and description are required.");
        }

        try {
            Story savedStory = storyService.saveStory(storyRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStory.getId());
        } catch (InvalidStoryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save story.");
        }
    }

    @GetMapping
    public ResponseEntity<List<StoryDto>> getAllStories() {
        return ResponseEntity.ok(storyService.getAllStories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryDto> getStoryById(@PathVariable Long id) {
        StoryDto story = storyService.getStoryById(id);
        return ResponseEntity.ok(story);
    }
}

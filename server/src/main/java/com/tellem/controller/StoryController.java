package com.tellem.controller;

import com.tellem.model.Choice;
import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.StoryRequest;
import com.tellem.repository.ChoiceRepository;
import com.tellem.repository.FrameRepository;
import com.tellem.repository.UserRepository;
import com.tellem.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryService storyService;
    private final UserRepository userRepository;
    private final FrameRepository frameRepository;
    private final ChoiceRepository choiceRepository;

    public StoryController(StoryService storyService, UserRepository userRepository, FrameRepository frameRepository, ChoiceRepository choiceRepository) {
        this.storyService = storyService;
        this.userRepository = userRepository;
        this.frameRepository = frameRepository;
        this.choiceRepository = choiceRepository;
    }

    @PostMapping
    public ResponseEntity<String> createStory(@RequestBody StoryRequest storyRequest) {
        if (storyRequest.getTitle().isEmpty() || storyRequest.getDescription().isEmpty() || storyRequest.getAuthorId().isEmpty()) {
            return new ResponseEntity<>("Title, content, and author are required", HttpStatus.BAD_REQUEST);
        }

        try {
            Story story = new Story();
            story.setTitle(storyRequest.getTitle());
            story.setDescription(storyRequest.getDescription());
            story.setAuthorId(storyRequest.getAuthorId(), userRepository);
            story.setFeatureImage(storyRequest.getFeatureImage());

            Story savedStory = storyService.saveStory(story);

            Map<String, Frame> frameMap = new HashMap<>();

            for (StoryRequest.FrameRequest frameRequest : storyRequest.getFrames()) {
                Frame frame = new Frame();
                frame.setContent(frameRequest.getContent());
                frame.setImage(frameRequest.getImage());
                frame.setFrameKey(frameRequest.getFrameKey());
                frame.setStory(savedStory);
                Frame savedFrame = frameRepository.save(frame);
                frameMap.put(frameRequest.getFrameKey(), savedFrame);
            }

            for (StoryRequest.FrameRequest frameRequest : storyRequest.getFrames()) {
                Frame currentFrame = frameMap.get(frameRequest.getFrameKey());
                if (frameRequest.getChoices() != null) {
                    for (StoryRequest.ChoiceRequest choiceRequest : frameRequest.getChoices()) {
                        Choice choice = new Choice();
                        choice.setName(choiceRequest.getName());
                        choice.setFrame(currentFrame);

                        choiceRepository.save(choice);

                        if (choiceRequest.getNextFrameKey() != null) {
                            Frame nextFrame = frameMap.get(choiceRequest.getNextFrameKey());
                            if (nextFrame != null) {

                                Choice nextChoice = new Choice();
                                nextChoice.setName("Go to next frame: " + nextFrame.getFrameKey());
                                nextChoice.setFrame(currentFrame);
                                choiceRepository.save(nextChoice);

                                currentFrame.getChoices().add(nextChoice);
                            }
                        }
                    }
                }
                frameRepository.save(currentFrame);
            }

            return new ResponseEntity<>(savedStory.getId().toString(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while creating the story: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Story>> getAllStories() {
        List<Story> stories = storyService.getAllStories();
        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Story> getStoryById(@PathVariable long id) {
        Story story = storyService.getStoryById(id);
        return new ResponseEntity<>(story, HttpStatus.OK);
    }
}

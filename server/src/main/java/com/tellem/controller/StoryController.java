package com.tellem.controller;

import com.tellem.model.Choice;
import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.User;
import com.tellem.model.dto.StoryRequest;
import com.tellem.repository.ChoiceRepository;
import com.tellem.repository.FrameRepository;
import com.tellem.repository.StoryRepository;
import com.tellem.repository.UserRepository;
import com.tellem.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    private final StoryRepository storyRepository;

    public StoryController(StoryService storyService, UserRepository userRepository, FrameRepository frameRepository, ChoiceRepository choiceRepository, StoryRepository storyRepository) {
        this.storyService = storyService;
        this.userRepository = userRepository;
        this.frameRepository = frameRepository;
        this.choiceRepository = choiceRepository;
        this.storyRepository = storyRepository;
    }

    @PostMapping
    public ResponseEntity<String> createStory(@RequestBody StoryRequest storyRequest) {
        // Validare input
        if (storyRequest.getTitle() == null || storyRequest.getTitle().isEmpty() ||
                storyRequest.getDescription() == null || storyRequest.getDescription().isEmpty()
        ) {
            return new ResponseEntity<>("Title, description, and author are required", HttpStatus.BAD_REQUEST);
        }

        // Găsirea autorului
        User author = userRepository.findById(storyRequest.getAuthorId());
        if (author == null) {
            return new ResponseEntity<>("Author not found", HttpStatus.NOT_FOUND);
        }

        try {
            // Crearea poveștii
            Story story = new Story();
            story.setTitle(storyRequest.getTitle());
            story.setDescription(storyRequest.getDescription());
            story.setAuthorId(storyRequest.getAuthorId(), userRepository);
            story.setFeatureImage(storyRequest.getFeatureImage());

            // Salvarea poveștii
            Story savedStory = storyService.saveStory(story);

            // Crearea și salvarea cadrelor
            Map<String, Frame> frameMap = new HashMap<>();
            for (StoryRequest.FrameRequest frameRequest : storyRequest.getFrames()) {
                // Verificăm dacă acest frame există deja în map
                Frame currentFrame = frameMap.get(frameRequest.getFrameKey());

                // Dacă nu există, îl creăm
                if (currentFrame == null) {
                    currentFrame = new Frame();
                    currentFrame.setFrameKey(frameRequest.getFrameKey());
                    currentFrame.setStory(story);  // Asociem acest frame cu story-ul

                    // Adăugăm acest frame în lista de frames a story-ului
                    if (story.getFrames() == null) {
                        story.setFrames(new ArrayList<>());
                    }


                    // Adăugăm frame-ul în mapa frameMap pentru a evita duplicarea
                    frameMap.put(frameRequest.getFrameKey(), currentFrame);
                }

                // Salvăm frame-ul înainte de a-l lega de alegere (Choice)
                frameRepository.save(currentFrame);
                story.getFrames().add(currentFrame);

                // Dacă sunt alegeri asociate acestui frame, le adăugăm
                if (frameRequest.getChoices() != null) {
                    for (StoryRequest.ChoiceRequest choiceRequest : frameRequest.getChoices()) {
                        Choice choice = new Choice();
                        choice.setName(choiceRequest.getName());
                        choice.setImage(choiceRequest.getImage());  // Dacă există imagine
                        choice.setFrame(currentFrame);  // Legăm alegerea de frame

                        // Legătura cu următorul frame, dacă există
                        if (choiceRequest.getNextFrameKey() != null) {
                            Frame nextFrame = frameMap.get(choiceRequest.getNextFrameKey());
                            if (nextFrame != null) {
                                choice.setNextFrameId(nextFrame.getFrameKey());
                            }
                        }

                        // Salvăm alegerea și o adăugăm la frame
                        choiceRepository.save(choice);
                        currentFrame.getChoices().add(choice);  // Adăugăm alegerea în frame
                    }
                }
            }

// Salvăm toate frame-urile (dacă nu le-ai salvat deja) într-o singură operație
            frameRepository.saveAll(story.getFrames());
            storyRepository.save(story);

            return new ResponseEntity<>("Story created successfully " + savedStory.getId(), HttpStatus.CREATED);
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
    public ResponseEntity<Story> getStoryById(@PathVariable String id) {
        Story story = storyService.getStoryById(String.valueOf(id));
        return new ResponseEntity<>(story, HttpStatus.OK);
    }
}

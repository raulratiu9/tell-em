package com.tellem.service;

import com.tellem.model.Choice;
import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.repository.ChoiceRepository;
import com.tellem.repository.FrameRepository;
import com.tellem.repository.StoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final FrameRepository frameRepository;
    private final ChoiceRepository choiceRepository;

    public StoryService(StoryRepository storyRepository, FrameRepository frameRepository, ChoiceRepository choiceRepository) {
        this.storyRepository = storyRepository;
        this.frameRepository = frameRepository;
        this.choiceRepository = choiceRepository;
    }

    @Transactional
    public Story saveStory(Story story) {
        // Save the story first so it has an ID
        Story savedStory = storyRepository.save(story);

        if (story.getFrames() != null && !story.getFrames().isEmpty()) {
            for (Frame frame : story.getFrames()) {
                // Link the frame to the saved story (ensure the frame knows the saved story ID)
                frame.setStory(savedStory);

                // Save the frame so it gets an ID (MongoDB needs this to create references)
                Frame savedFrame = frameRepository.save(frame);

                // After saving the frame, save the choices that refer to this frame
                if (frame.getChoices() != null && !frame.getChoices().isEmpty()) {
                    for (Choice choice : frame.getChoices()) {
                        // Ensure the choice references the saved frame
                        choice.setFrame(savedFrame);
                        choiceRepository.save(choice);  // Save the choice
                    }
                }
            }
        }

        return savedStory;  // Return the saved story
    }

    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    public Story getStoryById(String id) {
        return storyRepository.findById(id).orElse(null);
    }
}

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
        Story savedStory = storyRepository.save(story);

        if (story.getFrames() != null && !story.getFrames().isEmpty()) {
            for (Frame frame : story.getFrames()) {
                frame.setStory(savedStory);

                if (frame.getChoices() != null && !frame.getChoices().isEmpty()) {
                    for (Choice choice : frame.getChoices()) {
                        choice.setFrame(frame);
                        choiceRepository.save(choice);
                    }
                }

                frameRepository.save(frame);
            }
        }

        return savedStory;
    }

    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    public Story getStoryById(Long id) {
        return storyRepository.findById(id).orElse(null);
    }
}

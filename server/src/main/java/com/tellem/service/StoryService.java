package com.tellem.service;

import com.tellem.model.Story;
import com.tellem.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    public Story saveStory(Story story) {
        return storyRepository.save(story);
    }
}

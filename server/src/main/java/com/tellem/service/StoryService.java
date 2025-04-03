package com.tellem.service;

import com.tellem.model.Story;
import com.tellem.repository.StoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    public Mono<Story> getStoryByTitle(String title) {
        return Mono.justOrEmpty(storyRepository.findById(title));
    }

    public Story saveStory(Story story) {
        return storyRepository.save(story);
    }
}

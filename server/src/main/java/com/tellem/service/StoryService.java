package com.tellem.service;

import com.tellem.model.Story;
import com.tellem.repository.StoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public Flux<Story> getAllStories() {
        return storyRepository.findAll();
    }

    public Mono<Story> getStoryByTitle(String title) {
        return storyRepository.findByTitle(title);
    }
}

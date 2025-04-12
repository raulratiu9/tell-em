package com.tellem.service;

import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.repository.FrameRepository;
import com.tellem.repository.StoryRepository;
import org.neo4j.driver.Driver;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final Driver neo4jDriver;
    private final FrameRepository frameRepository;

    public StoryService(StoryRepository storyRepository, Driver neo4jDriver, FrameRepository frameRepository) {
        this.storyRepository = storyRepository;
        this.neo4jDriver = neo4jDriver;
        this.frameRepository = frameRepository;
    }

    public Story createGraphFromInput(StoryDto input) {
        Map<UUID, Frame> createdFrames = new HashMap<>();

        Story story = new Story();
        story.setTitle(input.getTitle());
        story.setDescription(input.getDescription());
        story.setFeatureImage(input.getFeatureImage());
        story.setStoryId(UUID.randomUUID());


        for (FrameDto frameDto : input.getFrames()) {
            Frame frame = new Frame();
            frame.setFrameId(frameDto.getFrameId());
            frame.setContent(frameDto.getContent());
            frame.setImage(frameDto.getImage());
            frame.setNextFrames(new ArrayList<>());

            createdFrames.put(frameDto.getFrameId(), frame);
        }

        if (input.getFrames() == null || input.getFrames().isEmpty()) {
            throw new IllegalArgumentException("Story must have at least one frame");
        }


        for (FrameDto frameDto : input.getFrames()) {
            Frame currentFrame = createdFrames.get(frameDto.getFrameId());

            if (frameDto.getNextFrames() != null) {
                for (FrameDto nextFrameDto : frameDto.getNextFrames()) {
                    Frame nextFrame = createdFrames.get(nextFrameDto.getFrameId());
                    if (nextFrame != null) {
                        currentFrame.getNextFrames().add(nextFrame);
                    }
                }
            }
        }


        story.setFirstFrame(createdFrames.get(input.getFrames().getFirst().getFrameId()));
        frameRepository.saveAll(createdFrames.values());
        return storyRepository.save(story).block();
    }


    public Flux<Story> getAllStories() {
        return storyRepository.findAll();
    }

    public Flux<Story> getStoryByTitle(String title) {
        return storyRepository.findByTitle(title);
    }
}
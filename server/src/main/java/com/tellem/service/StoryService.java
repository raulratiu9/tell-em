package com.tellem.service;

import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.repository.StoryRepository;
import org.neo4j.driver.Driver;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final Driver neo4jDriver;

    public StoryService(StoryRepository storyRepository, Driver neo4jDriver) {
        this.storyRepository = storyRepository;
        this.neo4jDriver = neo4jDriver;
    }

    public Story createGraphFromInput(StoryDto input) {
        Map<UUID, Frame> createdFrames = new HashMap<>();
        List<FrameDto> frameDtos = input.getFrames();

        Story story = new Story();
        story.setTitle(input.getTitle());
        story.setDescription(input.getDescription());
        story.setFeatureImage(input.getFeatureImage());
        story.setStoryId(UUID.randomUUID());


        for (FrameDto frameDto : frameDtos) {
            Frame frame = new Frame();
            frame.setFrameId(frameDto.getFrameId());
            frame.setContent(frameDto.getContent());
            frame.setImage(frameDto.getImage());

            createdFrames.put(frameDto.getFrameId(), frame);

            if (frameDto.getNextFrames() != null) {
                List<Frame> nextFrames = new ArrayList<>();
                for (FrameDto nextFrameDto : frameDto.getNextFrames()) {
                    Frame nextFrame = createdFrames.get(nextFrameDto.getFrameId());
                    if (nextFrame != null) {
                        nextFrames.add(nextFrame);
                    }
                }
                frame.setNextFrames(nextFrames);
            }
        }

        story.setFrames(new ArrayList<>(createdFrames.values()));

        return storyRepository.save(story).block();
    }


    public Flux<Story> getAllStories() {
        return storyRepository.findAll();
    }

    public Mono<Story> getStoryByTitle(String title) {
        return storyRepository.findByTitle(title);
    }
}
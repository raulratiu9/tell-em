package com.tellem.service;

import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.repository.StoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public Story createGraphFromInput(StoryDto input) {
        if (input.getFrames() == null || input.getFrames().isEmpty()) {
            throw new IllegalArgumentException("Story must have at least one frame");
        }

        Map<UUID, Frame> createdFrames = new HashMap<>();

        for (FrameDto frameDto : input.getFrames()) {
            Frame frame = new Frame();
            frame.setFrameId(frameDto.getFrameId());
            frame.setContent(frameDto.getContent());
            frame.setImage(frameDto.getImage());
            frame.setNextFrames(new ArrayList<>());
            createdFrames.put(frameDto.getFrameId(), frame);
        }

        for (FrameDto frameDto : input.getFrames()) {
            Frame currentFrame = createdFrames.get(frameDto.getFrameId());
            if (frameDto.getNextFrameIds() != null) {
                for (UUID nextId : frameDto.getNextFrameIds()) {
                    Frame nextFrame = createdFrames.get(nextId);
                    if (nextFrame != null) {
                        currentFrame.getNextFrames().add(nextFrame);
                    }
                }
            }
        }

        Set<UUID> branchingFramesId = new HashSet<>();
        for (FrameDto frameDto : input.getFrames()) {
            if (frameDto.getNextFrameIds() != null) {
                branchingFramesId.addAll(frameDto.getNextFrameIds());
            }
        }

        List<FrameDto> rootFrames = input.getFrames().stream()
                .filter(frame -> !branchingFramesId.contains(frame.getFrameId()))
                .toList();

        Frame actualRoot;
        if (rootFrames.size() > 1) {
            actualRoot = new Frame();
            actualRoot.setFrameId(UUID.randomUUID());
            actualRoot.setContent("Virtual Start Frame");
            actualRoot.setImage("virtual.png");
            actualRoot.setNextFrames(new ArrayList<>());

            for (FrameDto rootDto : rootFrames) {
                Frame root = createdFrames.get(rootDto.getFrameId());
                actualRoot.getNextFrames().add(root);
            }
        } else {
            actualRoot = createdFrames.get(rootFrames.get(0).getFrameId());
        }

        Story story = new Story();
        story.setTitle(input.getTitle());
        story.setDescription(input.getDescription());
        story.setFeatureImage(input.getFeatureImage());
        story.setStoryId(UUID.randomUUID());
        story.setFirstFrame(actualRoot);

        return storyRepository.save(story).block();
    }


    public Flux<Story> getAllStories() {
        return storyRepository.findAll();
    }

    public Mono<Story> getStoryByTitle(String title) {
        return storyRepository.findByTitle(title);
    }
}
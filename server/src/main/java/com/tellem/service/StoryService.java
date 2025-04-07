package com.tellem.service;

import com.tellem.model.Choice;
import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.repository.ChoiceRepository;
import com.tellem.repository.FrameRepository;
import com.tellem.repository.StoryRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.neo4j.driver.Values.parameters;

@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final FrameRepository frameRepository;
    private final ChoiceRepository choiceRepository;
    private final Driver neo4jDriver;

    public StoryService(StoryRepository storyRepository, FrameRepository frameRepository, ChoiceRepository choiceRepository, Driver neo4jDriver) {
        this.storyRepository = storyRepository;
        this.frameRepository = frameRepository;
        this.choiceRepository = choiceRepository;
        this.neo4jDriver = neo4jDriver;
    }

    public Story createGraphFromInput(StoryDto input) {
        Map<UUID, Frame> frameMap = new HashMap<>();
        Story story = new Story();
        story.setTitle(input.getTitle());
        story.setDescription(input.getDescription());
        story.setFeatureImage(input.getFeatureImage());
        story.setStoryId(UUID.randomUUID());
        try (Session session = neo4jDriver.session()) {
            for (FrameDto frameInput : input.getFrames()) {
                Frame currentFrame = new Frame();
                currentFrame.setFrameId(frameInput.getFrameId());
                currentFrame.setContent(frameInput.getContent());
                currentFrame.setImage(frameInput.getImage());
                currentFrame.setNextFrames(frameInput.getNextFrames());

                frameMap.put(currentFrame.getFrameId(), currentFrame);
                
                String createStoryFrameQuery = "MATCH (s:Story {storyId: $storyId}), (f:Frame {frameId: $frameId}) " +
                        "MERGE (s)-[:HAS_FRAMES]->(f)";
                session.run(createStoryFrameQuery, parameters(
                        "storyId", story.getStoryId().toString(), 
                        "frameId", currentFrame.getFrameId().toString()));

                if (frameInput.getNextFrames() != null) {
                    for (Choice link : frameInput.getNextFrames()) {
                        Choice choice = new Choice();
                        choice.setName(link.getName());
                        choice.setChoiceId(UUID.randomUUID());
                        choice.setNextFrameId(link.getNextFrameId());

                        choiceRepository.save(choice).block(); 

                        String createChoiceQuery = "CREATE (s:Choice {choiceId: $choiceId, name: $name, nextFrameId: $nextFrameId})";
                        session.run(createChoiceQuery, parameters(
                                "choiceId", choice.getChoiceId().toString(),
                                "name", choice.getName(),
                                "nextFrameId", choice.getNextFrameId()));

                        String createChoiceFrameLinkQuery = "MATCH (currentFrame:Frame {frameId: $frameId}), " +
                                "(choice:Choice {choiceId: $choiceId}) " +
                                "MERGE (currentFrame)-[:LEADS_TO]->(choice)";
                        session.run(createChoiceFrameLinkQuery, parameters(
                                "frameId", currentFrame.getFrameId().toString(),
                                "choiceId", choice.getChoiceId().toString()));

                        String linkChoiceToNextFrame = "MATCH (choice:Choice {choiceId: $choiceId}), " +
                                "(nextFrame:Frame {frameId: $nextFrameId}) " +
                                "MERGE (choice)-[:LEADS_TO]->(nextFrame)";
                        session.run(linkChoiceToNextFrame, parameters(
                                "choiceId", choice.getChoiceId().toString(),
                                "nextFrameId", link.getNextFrameId()));
                    }
                }
            }
        }

        story.setFrames(new ArrayList<>(frameMap.values()));
        return storyRepository.save(story).block();
    }


    public Flux<Story> getAllStories() {
        return storyRepository.findAll();
    }

    public Mono<Story> getStoryByTitle(String title) {
        return storyRepository.findByTitle(title);
    }
}
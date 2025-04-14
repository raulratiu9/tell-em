package com.tellem.service.benchmark;

import com.tellem.model.Choice;
import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.repository.ChoiceRepository;
import com.tellem.repository.FrameRepository;
import com.tellem.repository.StoryRepository;
import com.tellem.service.CSVLoggerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoryInsertionService implements StoryBuilderInterface {
    private final StoryRepository storyRepository;
    private final FrameRepository frameRepository;
    private final ChoiceRepository choiceRepository;

    public StoryInsertionService(
            StoryRepository storyRepository,
            FrameRepository frameRepository,
            ChoiceRepository choiceRepository
    ) {
        this.storyRepository = storyRepository;
        this.frameRepository = frameRepository;
        this.choiceRepository = choiceRepository;
    }


    @Override
    public Story createStory(String title, String description, String image) {
        Story story = new Story();
        story.setTitle(title);
        story.setDescription(description);
        story.setFeatureImage(image);
        return storyRepository.save(story);
    }

    @Override
    public Frame createFrame(int index, Story story) {
        Frame frame = new Frame();
        frame.setContent("This is a test node. Node " + index);
        frame.setImage("image.png");
        frame.setStory(story);
        frame.setFrameKey("frameKey" + index);
        return frameRepository.save(frame);
    }

    @Override
    public Choice createChoice(Frame fromFrame, Frame toFrame) {
        Choice choice = new Choice();
        choice.setName("Choice to frame " + toFrame.getFrameKey());
        choice.setImage("image.png");
        choice.setNextFrameId((toFrame.getId()));
        choice.setFrame(fromFrame);
        return choiceRepository.save(choice);
    }

    @Override
    public List<Frame> createFramesForStory(Story story, int frameCount) {
        List<Frame> frames = new ArrayList<>();
        for (int i = 0; i < frameCount; i++) {
            long start = System.currentTimeMillis();
            Frame frame = createFrame(i, story);
            long end = System.currentTimeMillis();

            CSVLoggerService.log("POSTGRESQL", "insert_node", i, end - start);
            System.out.println("Inserted Node " + i + " in " + (end - start) + " ms");

            frames.add(frame);
        }
        return frames;
    }

    @Override
    public void createChoicesBetweenFrames(List<Frame> frames) {
        for (int i = 0; i < frames.size() - 1; i++) {
            long start = System.currentTimeMillis();
            createChoice(frames.get(i), frames.get(i + 1));
            long end = System.currentTimeMillis();

            CSVLoggerService.log("POSTGRESQL", "insert_decision", i, end - start);
            System.out.println("Inserted decision from frame " + i + " in " + (end - start) + " ms");
        }
    }
}


package com.tellem.service;

import com.tellem.model.Choice;
import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.repository.ChoiceRepository;
import com.tellem.repository.FrameRepository;
import com.tellem.repository.StoryRepository;
import com.tellem.utils.TextGeneratorUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoryInsertionService {
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


    public Story createStory(String title) {
        Story story = new Story();
        story.setTitle(title);
        story.setDescription(TextGeneratorUtils.generateStoryDescription(story.getTitle()));
        story.setFeatureImage("https://tell-em-bucket.s3.eu-central-1.amazonaws.com/artistic_landscape_view_of_mountains_trees_lights_purple_starry_sky_moon_minimalism_4k_hd_minimalism.jpg");

        return storyRepository.save(story);
    }

    public Story saveStory(Story story) {
        return storyRepository.save(story);
    }


    public Frame createFrame(int index, Story story) {
        Frame frame = new Frame();
        frame.setContent(TextGeneratorUtils.generateFrameContent(String.valueOf(index)));
        frame.setImage("https://tell-em-bucket.s3.eu-central-1.amazonaws.com/artistic_mountains_moon_bird_trees_forest_purple_starry_sky_vaporwave_hd_vaporwave.jpg");
        frame.setStoryId(story.getId());
        return frameRepository.save(frame);
    }


    public Choice createChoice(Frame fromFrame, Frame toFrame) {
        Choice choice = new Choice();
        choice.setName(TextGeneratorUtils.generateChoiceText(fromFrame.getId()));
        choice.setImage("https://tell-em-bucket.s3.eu-central-1.amazonaws.com/purple_retro_wave_artistic_palm_trees_synthwave_moon_minimalism_vaporwave_4k_hd_vaporwave-2560x1440.jpg");
        choice.setNextFrameId((toFrame.getId()));
        return choiceRepository.save(choice);
    }


    public List<Frame> createFramesForStory(Story story, int frameCount) {
        List<Frame> frames = new ArrayList<>();
        for (int i = 0; i < frameCount; i++) {
            long start = System.currentTimeMillis();
            Frame frame = createFrame(i, story);
            long end = System.currentTimeMillis();

            System.out.println("Inserted Node " + i + " in " + (end - start) + " ms");
            frames.add(frame);
        }
        return frames;
    }


    public void createChoicesBetweenFrames(List<Frame> frames) {
        for (int i = 0; i < frames.size() - 1; i++) {
            long start = System.currentTimeMillis();
            createChoice(frames.get(i), frames.get(i + 1));
            long end = System.currentTimeMillis();

            System.out.println("Inserted decision from frame " + i + " in " + (end - start) + " ms");
        }
    }
}


package com.tellem.service.benchmark;

import com.tellem.model.Choice;
import com.tellem.model.Frame;
import com.tellem.model.Story;

import java.util.List;

public interface StoryBuilderInterface {
    Story createStory(String title, String description, String image);

    Frame createFrame(int index, Story story);

    Choice createChoice(Frame fromFrame, Frame toFrame);

    List<Frame> createFramesForStory(Story story, int frameCount);

    void createChoicesBetweenFrames(List<Frame> frames);
}

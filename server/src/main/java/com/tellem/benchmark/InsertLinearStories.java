package com.tellem.benchmark;

import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.service.StoryInsertionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InsertLinearStories {

    private final StoryInsertionService storyInsertionService;

    public InsertLinearStories(StoryInsertionService storyInsertionService) {
        this.storyInsertionService = storyInsertionService;
    }

    @Transactional
    public MultipleBenchmarkDto generate(int numberOfStories, int numberOfNodes) {
        long startStoryTime = System.currentTimeMillis();
        long totalMemoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double averageTimePerNode = 0;
        long totalTimeForNodes = 0;
        int totalChoices = 0;
        int totalNodes = 0;
        long totalTimeForChoices = 0;
        long memoryUsedForNodes = 0;

        for (int i = 0; i < numberOfStories; i++) {
            Story story = storyInsertionService.createStory("Story " + i);

            long startNodeTime = System.currentTimeMillis();
            long memoryBeforeNodes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            List<Frame> frames = storyInsertionService.createFramesForStory(story, numberOfNodes);
            long endNodeTime = System.currentTimeMillis();
            long memoryAfterNodes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            totalTimeForNodes += (endNodeTime - startNodeTime);
            averageTimePerNode = totalNodes > 0 ? (double) totalTimeForNodes / totalNodes : 0;
            memoryUsedForNodes = Math.max(0, memoryAfterNodes - memoryBeforeNodes);


            long startChoiceTime = System.currentTimeMillis();
            storyInsertionService.createChoicesBetweenFrames(frames);
            long endChoiceTime = System.currentTimeMillis();

            totalTimeForChoices += (endChoiceTime - startChoiceTime);
            totalChoices += Math.max(0, frames.size() - 1);

            story.setFrames(frames);
            story.setFirstFrame(frames.get(0));
            totalNodes += frames.size();
            storyInsertionService.saveStory(story);
        }

        long endStoryTime = System.currentTimeMillis();
        long totalTimeForStories = endStoryTime - startStoryTime;
        double averageTimePerStory = (double) totalTimeForStories / numberOfStories;

        long totalMemoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsedForStories = totalMemoryAfter - totalMemoryBefore;

        double avgTimePerChoice = totalChoices > 0 ? (double) totalTimeForChoices / totalChoices : 0;
        long memoryUsedForChoices = totalMemoryAfter - totalMemoryBefore;

        return new MultipleBenchmarkDto(
                numberOfStories,
                totalTimeForStories,
                averageTimePerStory,
                memoryUsedForStories,
                totalNodes,
                totalTimeForNodes,
                averageTimePerNode,
                memoryUsedForNodes,
                totalChoices,
                totalTimeForChoices,
                avgTimePerChoice,
                memoryUsedForChoices
        );
    }
}

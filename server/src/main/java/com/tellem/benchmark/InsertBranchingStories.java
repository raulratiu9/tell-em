package com.tellem.benchmark;

import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.service.StoryInsertionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class InsertBranchingStories {

    private final StoryInsertionService storyInsertionService;

    public InsertBranchingStories(StoryInsertionService storyInsertionService) {
        this.storyInsertionService = storyInsertionService;
    }

    public MultipleBenchmarkDto generate(int numberOfStories, int numberOfNodes, int storyDepth, int branchingFactor) {
        long memoryBeforeStory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long totalStoryTime = 0;
        long totalFrameTime = 0;
        long totalChoiceTime = 0;

        long memoryBeforeFrames = 0;
        long memoryAfterFrames = 0;
        long memoryBeforeChoices = 0;
        long memoryAfterChoices = 0;


        int totalFrames = 0;
        int totalChoices = 0;

        for (int i = 0; i < numberOfStories; i++) {
            long storyStart = System.currentTimeMillis();
            Story story = storyInsertionService.createStory("Story " + i);

            Frame root = storyInsertionService.createFrame(0, story);
            Queue<Frame> queue = new LinkedList<>();
            queue.add(root);
            List<Frame> allFrames = new ArrayList<>();
            allFrames.add(root);

            int created = 1;
            memoryBeforeFrames = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long frameStart = System.currentTimeMillis();
            while (created < numberOfNodes && !queue.isEmpty()) {
                Frame current = queue.poll();

                int branches = Math.min(branchingFactor, numberOfNodes - created);
                memoryBeforeChoices = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                for (int b = 0; b < branches && created < numberOfNodes; b++) {
                    Frame child = storyInsertionService.createFrame(created++, story);
                    long choiceStart = System.currentTimeMillis();
                    storyInsertionService.createChoice(current, child);
                    allFrames.add(child);
                    queue.add(child);
                    long choiceEnd = System.currentTimeMillis();

                    totalChoiceTime += (choiceEnd - choiceStart);
                    totalChoices++;
                }
            }
            long frameEnd = System.currentTimeMillis();

            memoryAfterFrames = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            memoryAfterChoices = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            long storyEnd = System.currentTimeMillis();
            totalStoryTime += (storyEnd - storyStart);
            totalFrameTime += (frameEnd - frameStart);
            totalStoryTime += (storyEnd - storyStart);
            totalFrames += allFrames.size();
        }

        long memoryUsedForStories = memoryBeforeFrames - memoryBeforeStory;
        long memoryUsedForFrames = memoryAfterFrames - memoryBeforeFrames;
        long memoryUsedForChoices = memoryAfterChoices - memoryBeforeChoices;

        double avgTimePerStory = (double) totalStoryTime / numberOfStories;
        double avgTimePerFrame = (double) totalFrameTime / totalFrames;
        double avgTimePerChoice = (double) totalChoiceTime / totalChoices;

        return new MultipleBenchmarkDto(
                numberOfStories, totalStoryTime, avgTimePerStory, memoryUsedForStories,
                totalFrames, totalFrameTime, avgTimePerFrame, memoryUsedForFrames,
                totalChoices, totalChoiceTime, avgTimePerChoice, memoryUsedForChoices
        );
    }
}

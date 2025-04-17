package com.tellem.benchmark;

import com.tellem.model.Choice;
import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.repository.FrameRepository;
import com.tellem.service.StoryInsertionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class InsertBranchingStories {

    private final StoryInsertionService storyInsertionService;
    private final FrameRepository frameRepository;

    public InsertBranchingStories(StoryInsertionService storyInsertionService, FrameRepository frameRepository) {
        this.storyInsertionService = storyInsertionService;
        this.frameRepository = frameRepository;
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
            long frameStart = System.currentTimeMillis();
            Frame rootFrame = storyInsertionService.createFrame(0, story);
            long frameEnd = System.currentTimeMillis();
            List<Frame> allFrames = new ArrayList<>();
            allFrames.add(rootFrame);

            Queue<Frame> frameQueue = new LinkedList<>();
            Queue<Integer> depthQueue = new LinkedList<>();
            frameQueue.add(rootFrame);
            depthQueue.add(1);

            int created = 1;
            memoryBeforeFrames = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            while (created < numberOfNodes && !frameQueue.isEmpty()) {
                Frame currentFrame = frameQueue.poll();
                Integer currentDepth = depthQueue.poll();

                if (currentDepth == null || currentDepth >= storyDepth) continue;

                List<Choice> currentChoices = new ArrayList<>();
                int branches = Math.min(branchingFactor, numberOfNodes - created);
                memoryBeforeChoices = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                for (int b = 0; b < branches && created < numberOfNodes; b++) {
                    Frame childFrame = storyInsertionService.createFrame(created++, story);
                    long choiceStart = System.currentTimeMillis();
                    Choice choice = storyInsertionService.createChoice(currentFrame, childFrame);
                    long choiceEnd = System.currentTimeMillis();

                    totalChoiceTime += (choiceEnd - choiceStart);
                    totalChoices++;
                    currentChoices.add(choice);

                    allFrames.add(childFrame);
                    frameQueue.add(childFrame);
                    depthQueue.add(currentDepth + 1);
                }

                currentFrame.setChoices(currentChoices);

                frameRepository.save(currentFrame);
                if (created >= numberOfNodes) {
                    break;
                }
            }

            memoryAfterFrames = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            memoryAfterChoices = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            

            story.setFrames(allFrames);
            story.setFirstFrame(allFrames.get(0));
            storyInsertionService.saveStory(story);

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

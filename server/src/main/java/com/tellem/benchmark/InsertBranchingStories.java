package com.tellem.benchmark;

import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.service.benchmark.StoryInsertionService;
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
        long startTime = System.currentTimeMillis();
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long totalInsertTime = 0;
        int totalFrames = 0;
        long memoryAfterNode = 0;

        for (int i = 0; i < numberOfStories; i++) {
            long storyStart = System.currentTimeMillis();
            Story story = storyInsertionService.createStory("Story " + i, "Description " + i, "image" + i + ".png");

            Frame root = storyInsertionService.createFrame(0, story);
            Queue<Frame> queue = new LinkedList<>();
            queue.add(root);
            List<Frame> allFrames = new ArrayList<>();
            allFrames.add(root);

            int created = 1;
            while (created < numberOfNodes && !queue.isEmpty()) {
                Frame current = queue.poll();

                int branches = Math.min(branchingFactor, numberOfNodes - created);

                for (int b = 0; b < branches && created < numberOfNodes; b++) {
                    Frame child = storyInsertionService.createFrame(created++, story);
                    storyInsertionService.createChoice(current, child);
                    allFrames.add(child);
                    queue.add(child);
                }
            }
            long storyEnd = System.currentTimeMillis();
            totalInsertTime += (storyEnd - storyStart);
            totalFrames += allFrames.size();
            memoryAfterNode = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTimePerNode = (double) totalInsertTime / totalFrames;

        return new MultipleBenchmarkDto(
                numberOfStories,
                totalTime,
                (double) totalTime / numberOfStories,
                memoryAfterNode - memoryBefore,
                numberOfNodes,
                totalInsertTime,
                avgTimePerNode,
                memoryAfterNode
        );
    }
}

package com.tellem.benchmark;

import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.BenchmarkResponseDto;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.service.BenchmarkService;
import com.tellem.service.benchmark.StoryInsertionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InsertStoriesWithNodes implements BenchmarkService {

    private final StoryInsertionService storyInsertionService;

    public InsertStoriesWithNodes(StoryInsertionService storyInsertionService) {
        this.storyInsertionService = storyInsertionService;
    }

    @Override
    @Transactional
    public MultipleBenchmarkDto runMultipleBenchmarks(int numberOfStories, int numberOfNodes) {
        long startStoryTime = System.currentTimeMillis();
        long totalMemoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long totalMemoryAfter;

        double averageTimePerNode = 0;
        long totalTimeForNodes = 0;
        for (int i = 0; i < numberOfStories; i++) {
            Story story = storyInsertionService.createStory(
                    "Story " + i,
                    "Benchmark story " + i,
                    "image" + i + ".png"
            );

            long startNodeTime = System.currentTimeMillis();
            List<Frame> frames = storyInsertionService.createFramesForStory(story, numberOfNodes);
            long endNodeTime = System.currentTimeMillis();

            totalTimeForNodes = endNodeTime - startNodeTime;
            averageTimePerNode = (double) totalTimeForNodes / numberOfNodes;

            storyInsertionService.createChoicesBetweenFrames(frames);
        }

        long endStoryTime = System.currentTimeMillis();
        long totalTimeForStories = endStoryTime - startStoryTime;
        double averageTimePerStory = (double) totalTimeForStories / numberOfStories;

        totalMemoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsedForStories = totalMemoryAfter - totalMemoryBefore;

        long memoryUsedForNodes = memoryUsedForStories / numberOfStories;

        return new MultipleBenchmarkDto(numberOfStories, totalTimeForStories, averageTimePerStory, memoryUsedForStories,
                numberOfNodes, totalTimeForNodes, averageTimePerNode, memoryUsedForNodes);
    }

    @Override
    public BenchmarkResponseDto runBenchmark(int numberOfItems) {
        return null;
    }
}

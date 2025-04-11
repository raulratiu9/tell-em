package com.tellem.benchmark;

import com.tellem.model.Story;
import com.tellem.model.dto.BenchmarkResponseDto;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.service.BenchmarkService;
import com.tellem.service.benchmark.BenchmarkUtils;
import com.tellem.service.benchmark.StoryInsertionService;
import org.springframework.stereotype.Service;

@Service
public class InsertNodes implements BenchmarkService {

    private final StoryInsertionService storyInsertionService;

    public InsertNodes(StoryInsertionService storyInsertionService) {
        this.storyInsertionService = storyInsertionService;
    }

    @Override
    public BenchmarkResponseDto runBenchmark(int numberOfItems) {
        long startTime = System.currentTimeMillis();

        Story story = storyInsertionService.createStory("Single Story", "Node insertion only", "feature.png");
        storyInsertionService.createFramesForStory(story, numberOfItems);

        long endTime = System.currentTimeMillis();
        return BenchmarkUtils.logTime(startTime, endTime, numberOfItems);
    }

    @Override
    public MultipleBenchmarkDto runMultipleBenchmarks(int numberOfStories, int numberOfNodes) {
        return null;
    }
}

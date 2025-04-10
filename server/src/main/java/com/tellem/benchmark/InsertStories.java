package com.tellem.benchmark;

import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.BenchmarkResponseDto;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.service.BenchmarkService;
import com.tellem.service.benchmark.BenchmarkUtils;
import com.tellem.service.benchmark.StoryInsertionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InsertStories implements BenchmarkService {

    private final StoryInsertionService storyInsertionService;
    private final int DEFAULT_NODES_CHOICES_NUMBER = 100;

    public InsertStories(StoryInsertionService storyInsertionService) {
        this.storyInsertionService = storyInsertionService;
    }

    @Override
    @Transactional
    public BenchmarkResponseDto runBenchmark(int numberOfItems) {
        long startStoryTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfItems; i++) {
            Story story = storyInsertionService.createStory("Test Story", "This is a benchmark story", "feature.png");
            List<Frame> frames = storyInsertionService.createFramesForStory(story, DEFAULT_NODES_CHOICES_NUMBER);
            storyInsertionService.createChoicesBetweenFrames(frames);
        }

        long endStoryTime = System.currentTimeMillis();

        return BenchmarkUtils.logTime(startStoryTime, endStoryTime, numberOfItems);
    }

    @Override
    public MultipleBenchmarkDto runMultipleBenchmarks(int numberOfStories, int numberOfNodes) {
        return null;
    }
}

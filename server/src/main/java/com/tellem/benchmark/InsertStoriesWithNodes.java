package com.tellem.benchmark;

import com.tellem.model.dto.BenchmarkResponseDto;
import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.service.BenchmarkService;
import com.tellem.service.StoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InsertStoriesWithNodes implements BenchmarkService {

    private final StoryService storyService;

    public InsertStoriesWithNodes(StoryService storyService) {
        this.storyService = storyService;
    }

    @Override
    public MultipleBenchmarkDto runMultipleBenchmarks(int numberOfStories, int numberOfNodes) {
        long startTime = System.currentTimeMillis();
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long nodeStart = 0;
        int totalNodes = 0;
        long nodeEnd = 0;
        long memoryAfterNode = 0;
        for (int i = 0; i <= numberOfStories - 1; i++) {
            StoryDto storyDto = generateStoryDto(i, numberOfNodes);
            storyService.createGraphFromInput(storyDto);
            nodeStart = System.currentTimeMillis();
            totalNodes = numberOfStories * numberOfNodes;

            storyService.createGraphFromInput(storyDto);
            nodeEnd = System.currentTimeMillis();
            memoryAfterNode = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        }
        long totalTimeForNodes = nodeEnd - nodeStart;
        double avgTimePerNode = (double) totalTimeForNodes / totalNodes;


        long endTime = System.currentTimeMillis();
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long totalTime = endTime - startTime;

        return new MultipleBenchmarkDto(
                numberOfStories,
                totalTime,
                (double) totalTime / numberOfStories,
                memoryAfter - memoryBefore,
                numberOfNodes,
                totalTimeForNodes, avgTimePerNode, memoryAfterNode
        );
    }

    private StoryDto generateStoryDto(int index, int numberOfNodes) {
        StoryDto storyDto = new StoryDto();
        storyDto.setTitle("Story " + UUID.randomUUID() + index);
        storyDto.setDescription("Description " + index);
        storyDto.setFeatureImage("image" + index + ".png");

        List<FrameDto> frames = new ArrayList<>();
        List<UUID> frameIds = new ArrayList<>();

        for (int i = 0; i < numberOfNodes; i++) {
            FrameDto frame = new FrameDto();
            UUID frameId = UUID.randomUUID();
            frame.setFrameId(frameId);
            frame.setContent("Content " + UUID.randomUUID() + i);
            frame.setImage("image" + i + ".png");
            frame.setNextFrames((frame.getNextFrames()));
            frames.add(frame);
            frameIds.add(frameId);
        }

        storyDto.setFrames(frames);
        return storyDto;
    }


    @Override
    public BenchmarkResponseDto runBenchmark(int numberOfItems) {
        return null;
    }
}

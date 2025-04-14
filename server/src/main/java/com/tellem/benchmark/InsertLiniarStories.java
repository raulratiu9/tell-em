package com.tellem.benchmark;

import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.service.StoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InsertLiniarStories {

    private final StoryService storyService;

    public InsertLiniarStories(StoryService storyService) {
        this.storyService = storyService;
    }

    public MultipleBenchmarkDto generate(int numberOfStories, int numberOfNodes) {
        long startTime = System.currentTimeMillis();
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long nodeStart = 0;
        int totalNodes = 0;
        long nodeEnd = 0;
        long memoryAfterNode = 0;
        for (int i = 0; i < numberOfStories; i++) {
            StoryDto storyDto = generateStoryDto(i, numberOfNodes);
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
        storyDto.setTitle("Story " + UUID.randomUUID() + "-" + index);
        storyDto.setDescription("Description " + index);
        storyDto.setFeatureImage("image" + index + ".png");

        List<FrameDto> frames = new ArrayList<>();

        for (int i = 0; i < numberOfNodes; i++) {
            FrameDto frame = new FrameDto();
            frame.setFrameId(UUID.randomUUID());
            frame.setContent("Content " + frame.getFrameId());
            frame.setImage("image" + (i % 10) + ".png");
            frame.setNextFrameIds(new ArrayList<>());
            frames.add(frame);
        }

        for (int i = 0; i < numberOfNodes - 1; i++) {
            FrameDto current = frames.get(i);
            FrameDto next = frames.get(i + 1);
            current.getNextFrameIds().add(next.getFrameId());
        }

        storyDto.setFrames(frames);
        storyDto.setFirstFrameId(frames.get(0).getFrameId());

        return storyDto;
    }

}

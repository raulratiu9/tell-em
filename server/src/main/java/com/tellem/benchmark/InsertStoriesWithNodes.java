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
        storyDto.setTitle("Story " + UUID.randomUUID() + index);
        storyDto.setDescription("Description " + index);
        storyDto.setFeatureImage("image" + index + ".png");

        List<FrameDto> frames = new ArrayList<>();

        // Creează toate frame-urile
        for (int i = 0; i < numberOfNodes; i++) {
            FrameDto frame = new FrameDto();
            frame.setFrameId(UUID.randomUUID());
            frame.setContent("Content " + UUID.randomUUID() + i);
            frame.setImage("image" + (i % 10) + ".png");
            frame.setNextFrames(new ArrayList<>());
            frames.add(frame);
        }

        int i = 0;
        while (i < numberOfNodes - 1) {
            FrameDto current = frames.get(i);

            if (i % 5 == 0 && i + 4 < numberOfNodes) {
                // ramificare: F_i → [F_i+1, F_i+2], continuări liniare
                FrameDto branch1 = frames.get(i + 1);
                FrameDto branch2 = frames.get(i + 2);
                FrameDto continue1 = frames.get(i + 3);
                FrameDto continue2 = frames.get(i + 4);

                current.setNextFrames(List.of(branch1, branch2));
                branch1.setNextFrames(List.of(continue1));
                branch2.setNextFrames(List.of(continue2));

                i += 1; // mergem la următorul nod liber (nu sărim peste tot)
            } else {
                // legătură simplă
                current.setNextFrames(List.of(frames.get(i + 1)));
                i += 1;
            }
        }

        storyDto.setFrames(frames);
        return storyDto;
    }


    @Override
    public BenchmarkResponseDto runBenchmark(int numberOfItems) {
        return null;
    }
}

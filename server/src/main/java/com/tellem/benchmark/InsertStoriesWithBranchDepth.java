package com.tellem.benchmark;

import com.tellem.model.dto.BenchmarkResponseDto;
import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.service.BenchmarkService;
import com.tellem.service.StoryService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InsertStoriesWithBranchDepth implements BenchmarkService {

    private final StoryService storyService;
    private int maxDepth = 2;
    private int branchFactor = 2;

    public InsertStoriesWithBranchDepth(StoryService storyService) {
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

        List<FrameDto> allFrames = new ArrayList<>();
        Queue<FrameDto> queue = new LinkedList<>();

        // 1. Creăm primul nod
        FrameDto root = createFrame(0);
        allFrames.add(root);
        queue.add(root);
        int created = 1;

        // 2. Generăm pe ramuri
        while (created < numberOfNodes && !queue.isEmpty()) {
            FrameDto current = queue.poll();

            // Ramificăm în 2
            int branches = Math.min(2, numberOfNodes - created);
            List<FrameDto> nextFrames = new ArrayList<>();

            for (int b = 0; b < branches; b++) {
                FrameDto child = createFrame(created++);
                nextFrames.add(child);
                queue.add(child);
                allFrames.add(child);

                // Continuăm ramura pe adâncime 1-2 dacă mai avem loc
                int depth = Math.min(2, numberOfNodes - created);
                FrameDto prev = child;
                for (int d = 0; d < depth; d++) {
                    FrameDto deep = createFrame(created++);
                    prev.setNextFrames(List.of(deep));
                    prev = deep;
                    queue.add(deep);
                    allFrames.add(deep);
                }
            }

            current.setNextFrames(nextFrames);
        }

        storyDto.setFrames(allFrames);
        return storyDto;
    }

    private FrameDto createFrame(int i) {
        FrameDto frame = new FrameDto();
        frame.setFrameId(UUID.randomUUID());
        frame.setContent("Content " + UUID.randomUUID() + i);
        frame.setImage("image" + (i % 10) + ".png");
        frame.setNextFrames(new ArrayList<>());
        return frame;
    }

    @Override
    public BenchmarkResponseDto runBenchmark(int numberOfItems) {
        return null;
    }

    public void setConfig(int maxDepth, int branchFactor) {
        this.maxDepth = maxDepth;
        this.branchFactor = branchFactor;
    }
}

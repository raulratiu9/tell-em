package com.tellem.benchmark;

import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.service.StoryService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InsertBranchingStories {

    private final StoryService storyService;
    private int storyDepth = 2;
    private int branchFactor = 2;

    public InsertBranchingStories(StoryService storyService) {
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
            StoryDto storyDto = generateStoryDto(i, numberOfNodes, storyDepth, branchFactor);
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

    private StoryDto generateStoryDto(int index, int numberOfNodes, int storyDepth, int branchFactor) {
        StoryDto storyDto = new StoryDto();
        storyDto.setTitle("Story " + UUID.randomUUID() + "-" + index);
        storyDto.setDescription("Description " + index);
        storyDto.setFeatureImage("image" + index + ".png");

        List<FrameDto> allFrames = new ArrayList<>();
        Map<UUID, FrameDto> frameMap = new HashMap<>();
        Queue<FrameDto> queue = new LinkedList<>();

        FrameDto root = createFrame(0);
        allFrames.add(root);
        frameMap.put(root.getFrameId(), root);
        queue.add(root);
        int created = 1;

        while (created < numberOfNodes && !queue.isEmpty()) {
            FrameDto current = queue.poll();
            List<UUID> nextIds = new ArrayList<>();

            int branches = Math.min(branchFactor, numberOfNodes - created);
            for (int b = 0; b < branches && created < numberOfNodes; b++) {
                FrameDto child = createFrame(created++);
                frameMap.put(child.getFrameId(), child);
                allFrames.add(child);
                queue.add(child);
                nextIds.add(child.getFrameId());

                FrameDto prev = child;
                for (int d = 0; d < storyDepth && created < numberOfNodes; d++) {
                    FrameDto deep = createFrame(created++);
                    frameMap.put(deep.getFrameId(), deep);
                    allFrames.add(deep);
                    prev.setNextFrameIds(List.of(deep.getFrameId()));
                    queue.add(deep);
                    prev = deep;
                }
            }

            current.setNextFrameIds(nextIds);
        }

        storyDto.setFrames(allFrames);
        storyDto.setFirstFrameId(root.getFrameId());
        return storyDto;
    }


    private FrameDto createFrame(int i) {
        FrameDto frame = new FrameDto();
        frame.setFrameId(UUID.randomUUID());
        frame.setContent("Content " + UUID.randomUUID() + i);
        frame.setImage("image" + (i % 10) + ".png");
        frame.setNextFrameIds(new ArrayList<>());
        return frame;
    }

    public void setConfig(int storyDepth, int branchFactor) {
        this.storyDepth = storyDepth;
        this.branchFactor = branchFactor;
    }
}

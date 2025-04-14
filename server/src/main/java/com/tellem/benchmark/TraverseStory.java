package com.tellem.benchmark;

import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.TraversalBenchmarkDto;
import com.tellem.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class TraverseStory {

    private final StoryRepository storyRepository;

    public TraverseStory(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public TraversalBenchmarkDto traverse(UUID storyId) {
        long startTime = System.currentTimeMillis();
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        Story story = storyRepository.findStory(storyId).block();
        if (story == null) {
            return new TraversalBenchmarkDto(
                    null, null,
                    0, 0, 0, 0,
                    "No story found in the database."
            );
        }

        Set<UUID> visited = new HashSet<>();
        int maxDepth = traverseStory(story.getFirstFrame(), visited, 1);

        long endTime = System.currentTimeMillis();
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        return new TraversalBenchmarkDto(
                story.getStoryId(),
                story.getTitle(),
                visited.size(),
                maxDepth,
                endTime - startTime,
                memoryAfter - memoryBefore,
                null // no error
        );
    }

    private int traverseStory(Frame frame, Set<UUID> visited, int currentDepth) {
        if (frame == null || frame.getFrameId() == null || visited.contains(frame.getFrameId())) {
            return currentDepth - 1;
        }

        visited.add(frame.getFrameId());
        int max = currentDepth;

        if (frame.getNextFrames() != null) {
            for (Frame next : frame.getNextFrames()) {
                max = Math.max(max, traverseStory(next, visited, currentDepth + 1));
            }
        }

        return max;
    }
}

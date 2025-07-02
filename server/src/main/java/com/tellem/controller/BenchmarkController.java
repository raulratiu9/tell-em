package com.tellem.controller;

import com.tellem.benchmark.InsertBranchingStories;
import com.tellem.benchmark.InsertLinearStories;
import com.tellem.benchmark.TraverseStory;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.model.dto.TraversalBenchmarkDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/benchmark")
public class BenchmarkController {

    private final InsertLinearStories insertLinearStoriesService;
    private final InsertBranchingStories insertBranchingStories;
    private final TraverseStory traverseStory;

    public BenchmarkController(
            InsertLinearStories insertLinearStoriesService, InsertBranchingStories insertBranchingStories, TraverseStory traverseStory) {
        this.insertLinearStoriesService = insertLinearStoriesService;
        this.insertBranchingStories = insertBranchingStories;
        this.traverseStory = traverseStory;
    }

    @GetMapping("/linear-stories")
    public ResponseEntity<MultipleBenchmarkDto> run(@RequestParam int numberOfStories,
                                                    @RequestParam int numberOfNodes) {
        MultipleBenchmarkDto benchmarkResponse = insertLinearStoriesService.generate(numberOfStories, numberOfNodes);

        return ResponseEntity.ok(benchmarkResponse);
    }

    @GetMapping("/branching-stories")
    public ResponseEntity<MultipleBenchmarkDto> runWithBranchDepth(
            @RequestParam int numberOfStories,
            @RequestParam int numberOfNodes,
            @RequestParam(defaultValue = "100") int storyDepth,
            @RequestParam(defaultValue = "100") int branchingFactor) {

        insertBranchingStories.setConfig(storyDepth, branchingFactor);
        MultipleBenchmarkDto benchmarkResponse =
                insertBranchingStories.generate(numberOfStories, numberOfNodes);

        return ResponseEntity.ok(benchmarkResponse);
    }

    @GetMapping("/traverse/{storyId}")
    public ResponseEntity<TraversalBenchmarkDto> runTraversalBenchmark(@PathVariable UUID storyId) {

        TraversalBenchmarkDto result = traverseStory.traverse(storyId);
        if (result.getErrorMessage() != null) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}
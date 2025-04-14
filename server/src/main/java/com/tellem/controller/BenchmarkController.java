package com.tellem.controller;

import com.tellem.benchmark.InsertBranchingStories;
import com.tellem.benchmark.InsertLiniarStories;
import com.tellem.model.dto.MultipleBenchmarkDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/benchmark")
public class BenchmarkController {

    private final InsertLiniarStories insertLiniarStories;
    private final InsertBranchingStories insertBranchingStories;

    public BenchmarkController(
            InsertLiniarStories insertLiniarStories, InsertBranchingStories insertBranchingStories) {
        this.insertLiniarStories = insertLiniarStories;
        this.insertBranchingStories = insertBranchingStories;
    }

    @GetMapping("/liniar-stories")
    public ResponseEntity<MultipleBenchmarkDto> run(@RequestParam int numberOfStories,
                                                    @RequestParam int numberOfNodes) {
        MultipleBenchmarkDto benchmarkResponse = insertLiniarStories.generate(numberOfStories, numberOfNodes);

        return ResponseEntity.ok(benchmarkResponse);
    }

    @GetMapping("/branching-stories")
    public ResponseEntity<MultipleBenchmarkDto> runWithBranchDepth(
            @RequestParam int numberOfStories,
            @RequestParam int numberOfNodes,
            @RequestParam int storyDepth,
            @RequestParam int branchingFactor) {


        MultipleBenchmarkDto benchmarkResponse =
                insertBranchingStories.generate(numberOfStories, numberOfNodes, storyDepth, branchingFactor);

        return ResponseEntity.ok(benchmarkResponse);
    }
}
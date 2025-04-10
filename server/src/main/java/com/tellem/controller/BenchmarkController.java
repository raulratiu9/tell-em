package com.tellem.controller;

import com.tellem.benchmark.InsertStoriesWithNodes;
import com.tellem.model.dto.BenchmarkResponseDto;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.service.BenchmarkService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/benchmark")
public class BenchmarkController {

    private final BenchmarkService insertStoriesService;
    private final InsertStoriesWithNodes insertStoriesWithNodesService;

    public BenchmarkController(
            @Qualifier("insertStories") BenchmarkService insertStoriesService, InsertStoriesWithNodes insertStoriesWithNodesService) {
        this.insertStoriesService = insertStoriesService;
        this.insertStoriesWithNodesService = insertStoriesWithNodesService;
    }

    @GetMapping("/stories-with-nodes")
    public ResponseEntity<MultipleBenchmarkDto> run(@RequestParam int numberOfStories,
                                                    @RequestParam int numberOfNodes) {
        MultipleBenchmarkDto benchmarkResponse = insertStoriesWithNodesService.runMultipleBenchmarks(numberOfStories, numberOfNodes);

        return ResponseEntity.ok(benchmarkResponse);
    }

    @GetMapping({"/stories/{number}", "/nodes/{number}"})
    public ResponseEntity<BenchmarkResponseDto> run(@PathVariable int number) {
        BenchmarkResponseDto benchmarkResponse = insertStoriesService.runBenchmark(number);
        return ResponseEntity.ok(benchmarkResponse);
    }
}
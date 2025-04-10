package com.tellem.service;

import com.tellem.model.dto.BenchmarkResponseDto;
import com.tellem.model.dto.MultipleBenchmarkDto;

public interface BenchmarkService {
    BenchmarkResponseDto runBenchmark(int numberOfItems);

    MultipleBenchmarkDto runMultipleBenchmarks(int numberOfStories, int numberOfNodes);

}

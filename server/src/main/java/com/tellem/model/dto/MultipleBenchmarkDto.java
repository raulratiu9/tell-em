package com.tellem.model.dto;

import lombok.Data;

@Data
public class MultipleBenchmarkDto {
    private BenchmarkResponseDto storiesResponse;
    private BenchmarkResponseDto nodesResponse;

    public MultipleBenchmarkDto(int insertedItemsStories, long totalTimeMillisStories, double averageTimePerStory, long memoryUsedBytesStories,
                                int insertedItemsNodes, long totalTimeMillisNodes, double averageTimePerNode, long memoryUsedBytesNodes) {
        this.storiesResponse = new BenchmarkResponseDto(insertedItemsStories, totalTimeMillisStories, averageTimePerStory, memoryUsedBytesStories);
        this.nodesResponse = new BenchmarkResponseDto(insertedItemsNodes, totalTimeMillisNodes, averageTimePerNode, memoryUsedBytesNodes);
    }
}

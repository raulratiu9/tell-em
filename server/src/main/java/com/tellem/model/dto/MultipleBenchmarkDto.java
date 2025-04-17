package com.tellem.model.dto;

import lombok.Data;

@Data
public class MultipleBenchmarkDto {
    private BenchmarkResponseDto storiesResponse;
    private BenchmarkResponseDto nodesResponse;
    private BenchmarkResponseDto choicesResponse;


    public MultipleBenchmarkDto(int insertedItemsStories, long totalTimeMillisStories, double averageTimePerStory, long memoryUsedBytesStories,
                                int insertedItemsNodes, long totalTimeMillisNodes, double averageTimePerNode, long memoryUsedBytesNodes,
                                int insertedItemsChoices, long totalTimeMillisChoices, double averageTimePerChoice, long memoryUsedBytesChoices) {
        this.storiesResponse = new BenchmarkResponseDto(insertedItemsStories, totalTimeMillisStories, averageTimePerStory, memoryUsedBytesStories);
        this.nodesResponse = new BenchmarkResponseDto(insertedItemsNodes, totalTimeMillisNodes, averageTimePerNode, memoryUsedBytesNodes);
        this.choicesResponse = new BenchmarkResponseDto(insertedItemsChoices, totalTimeMillisChoices, averageTimePerChoice, memoryUsedBytesChoices);
    }

    public MultipleBenchmarkDto(int numberOfStories, long totalTimeForStories, double averageTimePerStory, long memoryUsedForStories,
                                int numberOfNodes, long totalTimeForNodes, double averageTimePerNode, long memoryUsedForNodes) {
        this.storiesResponse = new BenchmarkResponseDto(numberOfStories, totalTimeForStories, averageTimePerStory, memoryUsedForStories);
        this.nodesResponse = new BenchmarkResponseDto(numberOfNodes, totalTimeForNodes, averageTimePerNode, memoryUsedForNodes);
        this.choicesResponse = new BenchmarkResponseDto(0, 0, 0, 0);
    }

}

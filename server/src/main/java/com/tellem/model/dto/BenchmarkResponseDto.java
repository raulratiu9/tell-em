package com.tellem.model.dto;

import lombok.Data;

@Data
public class BenchmarkResponseDto {
    private final int insertedItems;
    private final long totalTimeMillis;
    private final double averageTimePerNode;
    private final long memoryUsedBytes;

    public BenchmarkResponseDto(int insertedItems, long totalTimeMillis, double averageTimePerNode, long memoryUsedBytes) {
        this.insertedItems = insertedItems;
        this.totalTimeMillis = totalTimeMillis;
        this.averageTimePerNode = averageTimePerNode;
        this.memoryUsedBytes = memoryUsedBytes;
    }
}

package com.tellem.model.dto;

import lombok.Data;

@Data
public class BenchmarkResponseDto {
    private final int insertedItems;
    private final long totalTimeMillis;
    private final double averageTimePerItem;
    private final long memoryUsedBytes;

    public BenchmarkResponseDto(int insertedItems, long totalTimeMillis, double averageTimePerItem, long memoryUsedBytes) {
        this.insertedItems = insertedItems;
        this.totalTimeMillis = totalTimeMillis;
        this.averageTimePerItem = averageTimePerItem;
        this.memoryUsedBytes = memoryUsedBytes;
    }
}

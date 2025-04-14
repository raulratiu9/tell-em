package com.tellem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraversalBenchmarkDto {
    private UUID storyId;
    private String storyTitle;

    private int totalFramesTraversed;
    private int maxDepthReached;

    private long traversalTimeMs;
    private long memoryUsedBytes;

    private String errorMessage;
}

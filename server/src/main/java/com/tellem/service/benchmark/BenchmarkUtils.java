package com.tellem.service.benchmark;

import com.tellem.model.dto.BenchmarkResponseDto;
import com.tellem.service.CSVLoggerService;

public class BenchmarkUtils {
    public static BenchmarkResponseDto logTime(long start, long end, int numberOfItems) {
        long totalTimeElapsed = calculateTime(start, end);
        long memoryUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Total benchmark time: " + totalTimeElapsed + " ms & memoryUsed: " + memoryUsed + " bytes");
        CSVLoggerService.log("POSTGRESQL", "total_benchmark", numberOfItems, totalTimeElapsed);

        return new BenchmarkResponseDto(
                numberOfItems,
                totalTimeElapsed,
                (double) totalTimeElapsed / numberOfItems,
                memoryUsed
        );
    }

    public static long calculateTime(long startTime, long endTime) {
        return endTime - startTime;
    }
}

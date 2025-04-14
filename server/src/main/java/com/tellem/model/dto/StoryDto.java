package com.tellem.model.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class StoryDto {
    private String title;
    private String description;
    private String featureImage;
    private List<FrameDto> frames;
    private UUID firstFrameId;
    
}

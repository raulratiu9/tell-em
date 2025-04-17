package com.tellem.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class StoryDto {
    private String id;
    private String title;
    private String description;
    private String featureImage;
    private String firstFrameId;
    private List<FrameDto> frames;
}


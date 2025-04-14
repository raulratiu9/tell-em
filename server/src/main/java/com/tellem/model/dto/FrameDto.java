package com.tellem.model.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FrameDto {
    private UUID frameId;
    private String content;
    private String image;
    private List<UUID> nextFrameIds;
}



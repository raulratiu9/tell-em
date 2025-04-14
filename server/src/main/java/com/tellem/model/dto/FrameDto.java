package com.tellem.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class FrameDto {
    private Long frameId;
    private String content;
    private String image;
    private List<ChoiceDto> choices;
}


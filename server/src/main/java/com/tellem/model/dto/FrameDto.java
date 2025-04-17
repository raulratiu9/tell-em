package com.tellem.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class FrameDto {
    private String frameId;
    private String content;
    private String image;
    private List<ChoiceDto> choices;
}


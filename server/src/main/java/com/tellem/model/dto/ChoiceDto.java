package com.tellem.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChoiceDto {
    private String label;
    private UUID nextFrameId;

    public String getName() {
        return null;
    }
}

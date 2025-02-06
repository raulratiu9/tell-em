package com.tellem.model.dto;

import lombok.Data;

@Data
public class VoiceInfoDto {
    private String name;
    private String voice;

    public VoiceInfoDto(String name, String string) {
    }
}


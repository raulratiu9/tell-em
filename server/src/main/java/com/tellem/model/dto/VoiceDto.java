package com.tellem.model.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class VoiceDto {
    @Getter
    private String name;
    @Getter
    private String gender;
    private String displayName;

    public VoiceDto(String name, String gender, String displayName) {
        this.name = name;
        this.displayName = displayName;
        this.gender = gender;
    }
}

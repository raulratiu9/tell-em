package com.tellem.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class StoryRequest {
    private String title;
    private String description;
    private String featureImage;
    private String authorId;
    private List<FrameRequest> frames;

    @Data
    public static class FrameRequest {
        private String frameKey;
        private String content;
        private String image;
        private List<ChoiceRequest> choices;
    }

    @Data
    public static class ChoiceRequest {
        private String name;
        private String nextFrameKey;
    }
}


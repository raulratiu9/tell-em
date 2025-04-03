package com.tellem.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "stories")
@Data
public class Story {
    @Id
    @Field("story_id")
    private String id;
    private String title;
    private String description;
    private String featureImage;
    @Field("frames")
    private List<Frame> frames;
}

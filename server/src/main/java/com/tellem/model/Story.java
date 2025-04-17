package com.tellem.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "stories")
@Data
public class Story {

    @Id
    private String id;
    private String title;
    private String description;
    private String authorId;
    private String featureImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Frame firstFrame;

    @DBRef
    private List<Frame> frames;
}

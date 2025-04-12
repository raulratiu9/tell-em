package com.tellem.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Node("Story")
@Data
public class Story {
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private UUID storyId;
    @Property("title")
    private String title;
    @Property("description")
    private String description;
    @Property("featureImage")
    private String featureImage;
    @Relationship(type = "WRITTEN_BY", direction = Relationship.Direction.OUTGOING)
    private User author;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Relationship(type = "BEGINS_AT", direction = Relationship.Direction.OUTGOING)
    private Frame firstFrame;
}

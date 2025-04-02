package com.tellem.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.List;

@Node("Story")
@Data
public class Story {
    @Id
    private Long id;
    @Property("title")
    private String title;
    @Property("description")
    private String description;
    @Property("featureImage")
    private String featureImage;
    @Relationship(type = "WRITTEN_BY", direction = Relationship.Direction.OUTGOING)
    private User author;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Relationship(type = "HAS_FRAME", direction = Relationship.Direction.OUTGOING)
    private List<Frame> frames;
}

package com.tellem.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Frame")
@Data
public class Frame {
    @Id
    private Long id;
    @Property("title")
    private String title;
    @Property("content")
    private String content;
    @Property("image")
    private String image;

    @Relationship(type = "GOES_ON", direction = Relationship.Direction.OUTGOING)
    private List<Frame> nextFrames;
}


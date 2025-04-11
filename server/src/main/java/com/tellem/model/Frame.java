package com.tellem.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.UUID;

@Node("Frame")
@Data
public class Frame {
    @Id
    private UUID frameId;

    private String content;
    private String image;

    @Relationship(type = "LEADS_TO", direction = Relationship.Direction.OUTGOING)
    private List<Frame> nextFrames;
}



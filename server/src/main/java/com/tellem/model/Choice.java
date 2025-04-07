package com.tellem.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Data
@Node
public class Choice {
    @Id
    private UUID choiceId;

    private String name;

    private String nextFrameId;
}

package com.tellem.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "choices")
@Data
public class Choice {

    private String name;
    private String image;
    private String nextFrameId;
    private String currentFrameId;
}


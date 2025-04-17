package com.tellem.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "frames")
@Data
public class Frame {

    private String id;
    private String content;
    private String image;
    private String storyId;

    private List<Choice> choices = new ArrayList<>();
}


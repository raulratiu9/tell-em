package com.tellem.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "frames")
@Data
public class Frame {

    @Id
    private String id;
    private String content;
    private String frameKey;
    private String image;
    private String storyId;

    @DBRef
    private List<Choice> choices = new ArrayList<>();

    @DBRef
    private Story story;
}

package com.tellem.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "frames")
public class Frame {

    @Id
    private String id;
    private String content;
    private String frameKey;
    private String image;
    private String storyId;

    @DBRef
    private List<Choice> choices = new ArrayList<>(); // Initialize the choices list

    @DBRef
    private Story story;  // Reference to the story this frame belongs to

    // Getter and Setter methods
    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public void addChoice(Choice choice) {
        if (choices == null) {
            choices = new ArrayList<>();
        }
        choices.add(choice);
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getFrameKey() {
        return frameKey;
    }

    public void setFrameKey(String frameKey) {
        this.frameKey = frameKey;

    }

    public String getId() {
        return id;
    }

    public void setStory(Story savedStory) {
        this.story = savedStory;
    }

    // Other getter/setter methods...
}

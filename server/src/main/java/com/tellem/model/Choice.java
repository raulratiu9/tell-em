package com.tellem.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "choices")
public class Choice {

    private String currentFrameId;
    @Id
    private String id;
    private String name;
    private String image;
    private String nextFrameId;  // ID of the next frame

    @DBRef
    private Frame frame;  // Reference to the frame this choice belongs to

    public Choice() {
        this.currentFrameId = currentFrameId;
    }

    // Getter and Setter for frame
    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for image
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Getter and Setter for nextFrameId
    public String getNextFrameId() {
        return nextFrameId;
    }

    public void setNextFrameId(String nextFrameId) {
        this.nextFrameId = nextFrameId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCurrentFrameId(String id) {
        this.id = id;
    }
}

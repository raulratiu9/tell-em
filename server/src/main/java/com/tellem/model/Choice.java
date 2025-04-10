package com.tellem.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "choices")
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;

    private String name;

    private String image;

    private String nextFrameId;

    @ManyToOne
    @JoinColumn(name = "frame_id", nullable = false)
    private Frame frame;

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
    }

    public void setFrame(Frame frame) {
        if (frame != null) {
            this.frame = frame;
        } else {
            throw new IllegalArgumentException("Frame cannot be null.");
        }
    }

    public void setImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            this.image = imagePath;
        } else {
            throw new IllegalArgumentException("Image path cannot be null or empty.");
        }
    }

}


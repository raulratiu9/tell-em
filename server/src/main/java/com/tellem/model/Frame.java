package com.tellem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "frames")
@Data

public class Frame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "frame_id")
    private Long id;

    private String content;
    
    private String image;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @OneToMany(mappedBy = "frame", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Choice> choices;
}


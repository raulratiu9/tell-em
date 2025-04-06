package com.tellem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "frames")
@Data
public class Frame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String frameKey;

    @Column(length = 300)
    private String content;

    private String image;

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;

    @OneToMany(mappedBy = "frame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choices = new ArrayList<>();
}

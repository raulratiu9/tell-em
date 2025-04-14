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

    private Long nextFrameId;

    @ManyToOne
    @JoinColumn(name = "frame_id", nullable = false)
    private Frame frame;
}


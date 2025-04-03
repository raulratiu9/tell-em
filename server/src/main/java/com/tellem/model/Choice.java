package com.tellem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "choices")
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "frame_id", nullable = false)
    private Frame frame;
}


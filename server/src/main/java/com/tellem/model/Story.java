package com.tellem.model;

import com.tellem.model.dto.StoryRequest;
import com.tellem.repository.FrameRepository;
import com.tellem.repository.UserRepository;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stories")
@Data
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String featureImage;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Frame> frames;

    @ManyToOne
    @JoinColumn(name = "first_frame_id")
    private Frame firstFrame;

    public void setAuthorId(String authorId, UserRepository userRepository) {
        User user = userRepository.findById(authorId);
        this.author = user;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public void setContent(String content) {
    }

    public void setFrames(List<StoryRequest.FrameRequest> frames, FrameRepository frameRepository) {
    }

    public void setFirstFrame(Frame firstFrame) {
    }
}

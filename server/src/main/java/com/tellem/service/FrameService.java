package com.tellem.service;

import com.tellem.model.Frame;
import com.tellem.repository.FrameRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FrameService {
    private final FrameRepository frameRepository;

    public FrameService(FrameRepository frameRepository) {
        this.frameRepository = frameRepository;
    }

    public Flux<Frame> getAllFrames() {
        return frameRepository.findAll();
    }

    public Mono<Frame> getFrameByTitle(String title) {
        return frameRepository.findByTitle(title);
    }
}

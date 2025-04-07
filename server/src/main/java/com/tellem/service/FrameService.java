package com.tellem.service;

import com.tellem.model.Frame;
import com.tellem.repository.FrameRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class FrameService {
    private final FrameRepository frameRepository;

    public FrameService(FrameRepository frameRepository) {
        this.frameRepository = frameRepository;
    }

    public Flux<Frame> getAllFrames() {
        return frameRepository.findAll();
    }
}

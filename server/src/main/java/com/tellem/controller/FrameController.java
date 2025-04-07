package com.tellem.controller;

import com.tellem.model.Frame;
import com.tellem.service.FrameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/frames")
public class FrameController {
    private final FrameService frameService;

    public FrameController(FrameService frameService) {
        this.frameService = frameService;
    }

    @GetMapping
    public Flux<Frame> getAllFrames() {
        return frameService.getAllFrames();
    }
}

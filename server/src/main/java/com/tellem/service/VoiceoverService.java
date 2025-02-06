package com.tellem.service;

import com.tellem.model.dto.VoiceoverDto;
import com.tellem.repository.VoiceoverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoiceoverService {
    private final VoiceoverRepository voiceoverRepository;

    public VoiceoverService(VoiceoverRepository voiceoverRepository) {
        this.voiceoverRepository = voiceoverRepository;
    }

    public List<VoiceoverDto> getAllVoiceovers() {
        return voiceoverRepository.findAll();
    }

    public VoiceoverDto saveVoiceover(VoiceoverDto voiceover) {
        return voiceoverRepository.save(voiceover);
    }
}

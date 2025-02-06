package com.tellem.repository;

import com.tellem.model.dto.VoiceoverDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceoverRepository extends JpaRepository<VoiceoverDto, Long> {
}

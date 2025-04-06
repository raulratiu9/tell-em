package com.tellem.service;

import com.tellem.model.Choice;
import com.tellem.repository.ChoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChoiceService {

    private final ChoiceRepository choiceRepository;

    public ChoiceService(ChoiceRepository choiceRepository) {
        this.choiceRepository = choiceRepository;
    }

    public Choice saveChoice(Choice choice) {
        return choiceRepository.save(choice);
    }

    public List<Choice> getAllChoices() {
        return choiceRepository.findAll();
    }

    public Choice getChoiceById(Long id) {
        return choiceRepository.findById(id).orElse(null);
    }
}

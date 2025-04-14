package com.tellem.service;

import com.tellem.model.Choice;
import com.tellem.model.Frame;
import com.tellem.model.Story;
import com.tellem.model.dto.ChoiceDto;
import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.repository.ChoiceRepository;
import com.tellem.repository.FrameRepository;
import com.tellem.repository.StoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final FrameRepository frameRepository;
    private final ChoiceRepository choiceRepository;

    public StoryService(StoryRepository storyRepository, FrameRepository frameRepository, ChoiceRepository choiceRepository) {
        this.storyRepository = storyRepository;
        this.frameRepository = frameRepository;
        this.choiceRepository = choiceRepository;
    }

    @Transactional
    public Story saveStory(StoryDto storyDto) {
        Story story = new Story();
        story.setTitle(storyDto.getTitle());
        story.setDescription(storyDto.getDescription());
        story.setFeatureImage(storyDto.getFeatureImage());
        story.setCreatedAt(LocalDateTime.now());

        story = storyRepository.save(story);

        Map<Long, Frame> savedFrames = new HashMap<>();
        for (FrameDto frameDto : storyDto.getFrames()) {
            Frame frame = new Frame();
            frame.setId(frameDto.getFrameId());
            frame.setContent(frameDto.getContent());
            frame.setImage(frameDto.getImage());
            frame.setStory(story);
            frame = frameRepository.save(frame);
            savedFrames.put(frame.getId(), frame);
        }

        for (FrameDto frameDto : storyDto.getFrames()) {
            Frame parentFrame = savedFrames.get(frameDto.getFrameId());

            for (ChoiceDto choiceDto : frameDto.getChoices()) {
                Choice choice = new Choice();
                choice.setName(choiceDto.getName());
                choice.setImage(choiceDto.getImage());
                choice.setNextFrameId((choiceDto.getNextFrameId()));
                choice.setFrame(parentFrame);

                choiceRepository.save(choice);
            }
        }

        Frame firstFrame = savedFrames.get(storyDto.getFirstFrameId());
        story.setFirstFrame(firstFrame);

        return storyRepository.save(story);
    }

    public List<StoryDto> getAllStories() {
        return storyRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public StoryDto getStoryById(Long id) {
        Story story = storyRepository.findById(id).orElse(null);
        return mapToDto(story);
    }


    private StoryDto mapToDto(Story story) {
        StoryDto dto = new StoryDto();
        dto.setTitle(story.getTitle());
        dto.setDescription(story.getDescription());
        dto.setFeatureImage(story.getFeatureImage());

        List<FrameDto> frameDtos = story.getFrames().stream().map(frame -> {
            FrameDto frameDto = new FrameDto();
            frameDto.setFrameId(frame.getId());
            frameDto.setContent(frame.getContent());
            frameDto.setImage(frame.getImage());

            List<ChoiceDto> choiceDtos = frame.getChoices().stream().map(choice -> {
                ChoiceDto choiceDto = new ChoiceDto();
                choiceDto.setName(choice.getName());
                choiceDto.setImage(choice.getImage());
                choiceDto.setNextFrameId(Long.parseLong(String.valueOf(choice.getNextFrameId())));
                return choiceDto;
            }).toList();

            frameDto.setChoices(choiceDtos);
            return frameDto;
        }).toList();
        System.out.print(dto);
        dto.setFrames(frameDtos);
        return dto;
    }

}

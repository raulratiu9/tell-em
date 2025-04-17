package com.tellem.service;

import com.tellem.exception.StoryNotFoundException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tellem.utils.StoryValidationUtils.validateStory;
import static com.tellem.utils.StoryValidationUtils.validateStoryDto;

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

    public List<StoryDto> getAllStories() {
        return storyRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public StoryDto getStoryById(String id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new StoryNotFoundException(id));
        return mapToDto(story);
    }

    @Transactional
    public Story saveStory(StoryDto storyDto) {
        validateStoryDto(storyDto);
        Story story = createAndSaveStory(storyDto);
        Map<String, Frame> savedFrames = saveFrames(storyDto, story);
        saveChoices(storyDto, savedFrames);
        linkFramesToStory(storyDto, story, savedFrames);
        return storyRepository.save(story);
    }

    private Story createAndSaveStory(StoryDto storyDto) {
        Story story = new Story();
        story.setTitle(storyDto.getTitle());
        story.setDescription(storyDto.getDescription());
        story.setFeatureImage(storyDto.getFeatureImage());
        story.setCreatedAt(LocalDateTime.now());

        return storyRepository.save(story);
    }

    private Map<String, Frame> saveFrames(StoryDto storyDto, Story story) {
        Map<String, Frame> savedFrames = new HashMap<>();

        for (FrameDto frameDto : storyDto.getFrames()) {
            Frame frame = new Frame();
            frame.setId(frameDto.getFrameId());
            frame.setContent(frameDto.getContent());
            frame.setImage(frameDto.getImage());
            frame.setStoryId(story.getId());
            frame = frameRepository.save(frame);

            savedFrames.put(frame.getId(), frame);
        }
        return savedFrames;
    }

    private void saveChoices(StoryDto storyDto, Map<String, Frame> savedFrames) {
        for (FrameDto frameDto : storyDto.getFrames()) {
            Frame parentFrame = savedFrames.get(frameDto.getFrameId());
            List<Choice> savedChoices = new ArrayList<>();

            for (ChoiceDto choiceDto : frameDto.getChoices()) {
                Choice choice = new Choice();
                choice.setName(choiceDto.getName());
                choice.setImage(choiceDto.getImage());
                choice.setNextFrameId(choiceDto.getNextFrameId());
                savedChoices.add(choiceRepository.save(choice));
            }

            parentFrame.setChoices(savedChoices);
            frameRepository.save(parentFrame);
        }
    }

    private void linkFramesToStory(StoryDto storyDto, Story story, Map<String, Frame> savedFrames) {
        Frame firstFrame = savedFrames.get(storyDto.getFirstFrameId());
        story.setFirstFrame(firstFrame);
        story.setFrames(new ArrayList<>(savedFrames.values()));
    }

    private StoryDto mapToDto(Story story) {
        validateStory(story);

        StoryDto dto = new StoryDto();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setDescription(story.getDescription());
        dto.setFeatureImage(story.getFeatureImage());

        if (story.getFirstFrame() != null) {
            dto.setFirstFrameId(story.getFirstFrame().getId());
        }

        dto.setFrames(mapFrames(story.getFrames()));
        return dto;
    }

    private List<FrameDto> mapFrames(List<Frame> frames) {
        if (frames == null) return new ArrayList<>();

        return frames.stream()
                .map(this::mapFrame)
                .toList();
    }

    private FrameDto mapFrame(Frame frame) {
        FrameDto frameDto = new FrameDto();
        frameDto.setFrameId(frame.getId());
        frameDto.setContent(frame.getContent());
        frameDto.setImage(frame.getImage());
        frameDto.setChoices(mapChoices(frame.getChoices()));
        return frameDto;
    }

    private List<ChoiceDto> mapChoices(List<Choice> choices) {
        if (choices == null) return new ArrayList<>();

        return choices.stream()
                .map(choice -> {
                    ChoiceDto dto = new ChoiceDto();
                    dto.setName(choice.getName());
                    dto.setImage(choice.getImage());
                    dto.setNextFrameId(choice.getNextFrameId());
                    return dto;
                })
                .toList();
    }
}




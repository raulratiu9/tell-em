package com.tellem.utils;

import com.tellem.exception.InvalidStoryException;
import com.tellem.model.Story;
import com.tellem.model.dto.StoryDto;

public class StoryValidationUtils {
    public static void validateStory(Story story) {
        if (isNullOrEmpty(String.valueOf(story.getId())) ||
                isNullOrEmpty(story.getTitle()) ||
                isNullOrEmpty(story.getDescription()) ||
                isNullOrEmpty(story.getFeatureImage())) {
            throw new InvalidStoryException("The story must have a valid id, title, description, and feature image.");
        }
    }

    public static void validateStoryDto(StoryDto storyDto) {
        if (isNullOrEmpty(storyDto.getTitle()) || isNullOrEmpty(storyDto.getDescription()) || isNullOrEmpty(storyDto.getFeatureImage())) {
            throw new InvalidStoryException("Story must have a title, description and feature image.");
        }
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}

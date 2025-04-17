package com.tellem.exception;

public class StoryNotFoundException extends RuntimeException {
    public StoryNotFoundException(String id) {
        super("Story with ID " + id + " was not found.");
    }
}

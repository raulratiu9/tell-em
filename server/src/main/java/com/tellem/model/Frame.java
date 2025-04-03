package com.tellem.model;

import lombok.Data;

import java.util.List;

@Data
public class Frame {
    private String id;

    private String title;

    private String content;

    private String image;

    private List<Choice> choices;
}

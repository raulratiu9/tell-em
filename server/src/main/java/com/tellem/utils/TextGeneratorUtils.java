package com.tellem.utils;

import java.util.Random;

public class TextGeneratorUtils {
    private static final String[] LOREM_VARIANTS = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            "Pellentesque habitant morbi tristique senectus et netus.",
            "Vivamus suscipit tortor eget felis porttitor volutpat.",
            "Curabitur arcu erat, accumsan id imperdiet et, porttitor at sem.",
            "Quisque velit nisi, pretium ut lacinia in, elementum id enim."
    };

    private static final Random RANDOM = new Random();

    public static String generateStoryDescription(String title) {
        String base = "Story " + title + ": Lorem ipsum dolor sit amet, consectetur adipiscing elit. ";

        return generateText(base, 500);
    }

    public static String generateFrameContent(int frameId) {
        String base = "Frame " + frameId + ": Lorem ipsum dolor sit amet, consectetur adipiscing elit. ";

        return generateText(base, 300);
    }

    public static String generateChoiceText(Long nextFrameId) {
        String base = "Choice " + nextFrameId + ": ";

        return generateText(base, 100);
    }

    public static String generateText(String base, int characters) {
        if (base.length() > characters) {
            return base.substring(0, characters);
        }

        StringBuilder sb = new StringBuilder(base);
        while (sb.length() < characters) {
            sb.append(LOREM_VARIANTS[RANDOM.nextInt(LOREM_VARIANTS.length)]).append(" ");
        }
        return sb.substring(0, characters);
    }
}

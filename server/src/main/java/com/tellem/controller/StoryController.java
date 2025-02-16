package com.tellem.controller;

import com.tellem.model.Story;
import com.tellem.repository.UserRepository;
import com.tellem.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryService storyService;
    private final UserRepository userRepository; // Inject the UserRepository

    public StoryController(StoryService storyService, UserRepository userRepository) {
        this.storyService = storyService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Story>> getAllStories() {
        List<Story> stories = storyService.getAllStories();
        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Story> getStoryById(@PathVariable long id) {
        Story story = storyService.getStoryById(id);
        return new ResponseEntity(story, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createStory(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("image") MultipartFile image,
            @RequestParam("author_id") String authorId) {

        try {
            Story story = new Story();

            // Handle file upload
            if (image != null && !image.isEmpty()) {
                System.out.println("Image received: " + image.getOriginalFilename());
                String imagePath = saveImage(image);
                story.setImage(imagePath);
                System.out.println("Image saved at: " + imagePath);
            } else {
                System.out.println("No image provided");
            }

            // Set the author based on author_id
            story.setTitle(title);
            story.setContent(content);
            story.setAuthorId(authorId, userRepository); // Set the author using the new method

            Story savedStory = storyService.saveStory(story);

            return new ResponseEntity(savedStory.getId().toString(), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Save image to local file system
    private String saveImage(MultipartFile image) throws IOException {
        // Create the uploads directory if it doesn't exist
        String uploadDir = "src/main/resources/static";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // Get the original file name
        String originalFileName = image.getOriginalFilename();
        if (originalFileName != null) {
            // Generate a unique name for the file
            String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
            Path path = Paths.get(uploadDir + File.separator + uniqueFileName);

            // Save the file to the disk
            Files.copy(image.getInputStream(), path);

            return uniqueFileName;  // Return the file path
        } else {
            throw new IOException("File name is invalid.");
        }
    }
}

package com.tellem.controller;

import com.tellem.service.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private AwsS3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // Încărcăm fiecare fișier și adăugăm URL-ul la lista de URL-uri
                String fileUrl = s3Service.uploadFile(file);
                fileUrls.add(fileUrl);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(List.of("Eroare la încărcarea fișierului: " + e.getMessage()));
            }
        }

        // Returnează lista de URL-uri pentru fișierele încărcate
        return ResponseEntity.ok(fileUrls);
    }
}

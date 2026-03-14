package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final String uploadDir = "uploads";

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File is empty"));
        }

        try {
            // Create directory if not exists
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // Clean filename and add UUID
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = path.resolve(fileName);

            // Copy file to target location
            Files.copy(file.getInputStream(), filePath);

            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", fileName));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Could not upload file: " + e.getMessage()));
        }
    }
}

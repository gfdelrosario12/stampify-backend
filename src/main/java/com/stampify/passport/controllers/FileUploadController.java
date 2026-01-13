package com.stampify.passport.controllers;

import com.stampify.passport.services.S3FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private S3FileStorageService s3FileStorageService;

    @PostMapping("/venue-image")
    public ResponseEntity<Map<String, String>> uploadVenueImage(@RequestParam("file") MultipartFile file) throws IOException {
        String url = s3FileStorageService.uploadVenueImage(file);
        Map<String, String> response = new HashMap<>();
        response.put("venueImageUrl", url);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/event-badge")
    public ResponseEntity<Map<String, String>> uploadEventBadge(@RequestParam("file") MultipartFile file) throws IOException {
        String url = s3FileStorageService.uploadEventBadge(file);
        Map<String, String> response = new HashMap<>();
        response.put("eventBadgeUrl", url);
        return ResponseEntity.ok(response);
    }
}

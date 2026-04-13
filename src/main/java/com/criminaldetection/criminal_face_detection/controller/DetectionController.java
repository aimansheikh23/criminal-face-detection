package com.criminaldetection.criminal_face_detection.controller;

import com.criminaldetection.criminal_face_detection.service.DetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/detector")
public class DetectionController {

    @Autowired
    private DetectionService detectionService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("image") MultipartFile file, @RequestParam Integer operator_id) {
        String result = detectionService.processDetection(file, operator_id);
        return ResponseEntity.ok(result);
    }
}

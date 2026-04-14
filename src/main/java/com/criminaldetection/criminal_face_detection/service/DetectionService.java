package com.criminaldetection.criminal_face_detection.service;

import com.criminaldetection.criminal_face_detection.entity.Criminal;
import com.criminaldetection.criminal_face_detection.entity.CriminalPhoto;
import com.criminaldetection.criminal_face_detection.entity.DetectionLogs;
import com.criminaldetection.criminal_face_detection.entity.User;
import com.criminaldetection.criminal_face_detection.repository.CriminalPhotoRepository;
import com.criminaldetection.criminal_face_detection.repository.DetectionLogRepository;
import com.criminaldetection.criminal_face_detection.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DetectionService {

    @Value("${detection.confidence.threshold}")
    private double threshold;

    @Autowired
    private MinIoService minIoService;

    @Autowired
    private FaceApiService  faceApiService;

    @Autowired
    private CriminalPhotoRepository criminalPhotoRepository;

    @Autowired
    private DetectionLogRepository detectionLogRepository;


    @Autowired
    private UserRepository  userRepository;


    public String processDetection(MultipartFile image, Integer operatorId) {
        String objectKey = minIoService.uploadImage(image);

        FaceApiService.FaceSearchResult result = faceApiService.searchFace(objectKey);

        User operator = userRepository.findById(operatorId).orElseThrow(() -> new RuntimeException("Operator not found"));

        DetectionLogs detectionLogs = new DetectionLogs();
        detectionLogs.setOperator(operator);
        detectionLogs.setUploadImageKey(objectKey);
        detectionLogs.setDetectedAt(LocalDateTime.now());

        if (result.faceToken != null && result.confidence >= threshold) {
            CriminalPhoto bestMatch = criminalPhotoRepository.findByFaceToken(result.faceToken).orElse(null);
            if (bestMatch != null) {
                Criminal matchedCriminal = bestMatch.getCriminal();
                detectionLogs.setIsMatched(true);
                detectionLogs.setCriminal(matchedCriminal);
                detectionLogs.setConfidenceScore(result.confidence);

                detectionLogRepository.save(detectionLogs);


                return "Match found: " + matchedCriminal.getFullName()
                        + " | Confidence: " + String.format("%.0f", result.confidence) + "%";
            }
        }

        // No match
        detectionLogs.setIsMatched(false);
        detectionLogs.setConfidenceScore(0.0);
        detectionLogRepository.save(detectionLogs);
        return "No match found in the criminal database";

    }
}

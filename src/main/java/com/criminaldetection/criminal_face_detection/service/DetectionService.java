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
    private AlertService alertService;

    @Autowired
    private UserRepository  userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String processDetection(MultipartFile image, Integer operatorId) {
        String objectKey = minIoService.uploadImage(image);

        String imageUrl = minIoService.getImageUrl(objectKey);

        List<Float> embedding = faceApiService.extractFaceEmbeddings(imageUrl);

        CriminalPhoto bestMatch = findMatchingCriminal(embedding);

        User operator = userRepository.findById(operatorId).orElseThrow(() -> new RuntimeException("Operator not found"));

        DetectionLogs detectionLogs = new DetectionLogs();
        detectionLogs.setOperator(operator);
        detectionLogs.setUpload_image_key(objectKey);
        detectionLogs.setDetected_at(LocalDateTime.now());

        if (bestMatch != null) {
            Criminal matchedCriminal = bestMatch.getCriminal();
            double score = calculateConfidence(embedding, parseEmbedding(bestMatch.getEmbedding()));
            detectionLogs.setIs_matched(true);
            detectionLogs.setCriminal(matchedCriminal);
            detectionLogs.setConfidence_score(score);

            detectionLogRepository.save(detectionLogs);

            alertService.createAlert(detectionLogs, matchedCriminal, score);

            return "Match found: " + matchedCriminal.getFull_name()
                    + " | Confidence: " + String.format("%.0f", score * 100) + "%";
        }else {
            // No match
            detectionLogs.setIs_matched(false);
            detectionLogs.setConfidence_score(0.0);
            detectionLogRepository.save(detectionLogs);
            return "No match found in the criminal database";
        }

    }


    private CriminalPhoto findMatchingCriminal(List<Float> newEmbeddings) {
        List<CriminalPhoto> allPhotos = criminalPhotoRepository.findAll();

        CriminalPhoto  bestMatch = null;
        double bestScore = 0.0;

        for(CriminalPhoto photo : allPhotos) {
            if (photo.getEmbedding() == null){
                continue;
            }
            List<Float> embedding = parseEmbedding(photo.getEmbedding());
            double confidenceScore = calculateConfidence(newEmbeddings, embedding);
            if (confidenceScore > bestScore) {
                bestScore  = confidenceScore;
                bestMatch = photo;
            }
        }
        return bestScore >= threshold ? bestMatch : null;
    }

    private double calculateConfidence(List<Float> a, List<Float> b) {
        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (int i = 0; i<a.size(); i++) {
            dotProduct += a.get(i) * b.get(i);
            magnitudeA += a.get(i) * a.get(i);
            magnitudeB += b.get(i) * b.get(i);
        }

        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);

        if (magnitudeA == 0.0 || magnitudeB == 0.0) {
            return 0.0;
        }

        return dotProduct / (magnitudeA * magnitudeB);
    }

    private List<Float> parseEmbedding(String embeddingJson){
        try {
            return objectMapper.readValue(embeddingJson, new TypeReference<List<Float>>() {});
        }catch (Exception e){
            throw new RuntimeException("Failed to parse embedding: " + e.getMessage());
        }
    }


}

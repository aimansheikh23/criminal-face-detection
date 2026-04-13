package com.criminaldetection.criminal_face_detection.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "detection_log")
@Getter
@Setter
public class DetectionLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator;

    @ManyToOne
    @JoinColumn(name = "matched_criminal_id")
    private Criminal criminal;

    private  String uploadImageKey;

    private double confidenceScore;

    private Boolean isMatched;

    private LocalDateTime detectedAt;

    public DetectionLogs() {}

    public DetectionLogs(User operator, Criminal criminal, String upload_image_key, double confidence_score, Boolean is_matched) {
        this.operator = operator;
        this.criminal = criminal;
        this.uploadImageKey = upload_image_key;
        this.confidenceScore = confidence_score;
        this.isMatched = is_matched;
        this.detectedAt = LocalDateTime.now();
    }

}

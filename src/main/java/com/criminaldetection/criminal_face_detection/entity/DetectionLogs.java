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

    private  String upload_image_key;

    private double confidence_score;

    private Boolean is_matched;

    private LocalDateTime detected_at;

    public DetectionLogs() {}

    public DetectionLogs(User operator, Criminal criminal, String upload_image_key, double confidence_score, Boolean is_matched,  LocalDateTime detected_at) {
        this.operator = operator;
        this.criminal = criminal;
        this.upload_image_key = upload_image_key;
        this.confidence_score = confidence_score;
        this.is_matched = is_matched;
        this.detected_at = detected_at;
    }

}

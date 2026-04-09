package com.criminaldetection.criminal_face_detection.dto;

import com.criminaldetection.criminal_face_detection.enums.CrimeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AlertResponse {
    private String criminalName;
    private CrimeType crimeType;
    private double confidence_score;
    private String message;
    private LocalDateTime timestamp;
}

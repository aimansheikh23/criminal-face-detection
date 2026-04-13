package com.criminaldetection.criminal_face_detection.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FaceSearchResponse {
    private String faceToken;
    private double confidence;
    private String message;
}

package com.criminaldetection.criminal_face_detection.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FaceEmbeddingResponse {
    private List<Float> faceEmbeddings;

    private String message;
}

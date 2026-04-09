package com.criminaldetection.criminal_face_detection.service;

import com.criminaldetection.criminal_face_detection.dto.FaceEmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class FaceApiService {

    @Autowired
    private WebClient webClient;

    public List<Float> extractFaceEmbeddings(String imageUrl) {
        FaceEmbeddingResponse response = webClient.post().uri("/extract-embedding")
                .bodyValue(Map.of("image_url",imageUrl))
                .retrieve().bodyToMono(FaceEmbeddingResponse.class).block();
        if (response == null || response.getFaceEmbeddings() == null) {
            throw new RuntimeException("No face detected in the image");
        }
        return response.getFaceEmbeddings();
    }
}

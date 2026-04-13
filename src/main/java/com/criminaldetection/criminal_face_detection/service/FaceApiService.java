package com.criminaldetection.criminal_face_detection.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class FaceApiService {

    @Value("${facepp.api.key}")
    private String apiKey;

    @Value("${facepp.api.secret}")
    private String apiSecret;

    @Value("${facepp.faceset.token}")
    private String faceSetToken;

    @Value("${minio.bucket}")
    private String bucket;

    private final MinioClient minioClient;
    private final WebClient webClient;

    public FaceApiService(WebClient webClient,  MinioClient minioClient) {
        this.webClient = webClient;
        this.minioClient = minioClient;
    }

    public String toBase64(String objectKey){
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucket)
                            .object(objectKey).build()
            );
            byte[] bytes = stream.readAllBytes();
            stream.close();
            return Base64.getEncoder().encodeToString(bytes);
        }catch (Exception e){
            throw new RuntimeException("Failed to read image from MinIO: " + e.getMessage());
        }
    }

    public String detectFace(String objectKey) {
        String imageBase64 = toBase64(objectKey);
        Map response = webClient.post()
                .uri("https://api-us.faceplusplus.com/facepp/v3/detect")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("api_key", apiKey)
                        .with("api_secret", apiSecret)
                        .with("image_base64", imageBase64))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Face++ detect error: " + body))
                )
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            throw new RuntimeException("No response from Face++ detect");
        }

        List<Map> faces = (List<Map>) response.get("faces");

        if (faces == null || faces.isEmpty()) {
            throw new RuntimeException("No face detected in the image");
        }
        return faces.get(0).get("face_token").toString();
    }

    public String addFaceToFaceSet(String objectKey) {
        String faceToken = detectFace(objectKey);

        Map response = webClient.post()
                .uri("https://api-us.faceplusplus.com/facepp/v3/faceset/addface")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("api_key", apiKey)
                        .with("api_secret", apiSecret)
                        .with("faceset_token", faceSetToken)
                        .with("face_tokens", faceToken))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Face++ addface error: " + body))
                )
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            throw new RuntimeException("Failed to add face to FaceSet");
        }

        return faceToken;
    }

    // Step 3 — search faceset for a matching face
    public FaceSearchResult searchFace(String objectKey) {
        String imageBase64 = toBase64(objectKey);
        Map response = webClient.post()
                .uri("https://api-us.faceplusplus.com/facepp/v3/search")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("api_key", apiKey)
                        .with("api_secret", apiSecret)
                        .with("faceset_token", faceSetToken)
                        .with("image_base64", imageBase64)
                        .with("return_result_count", "1"))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Face++ search error: " + body))
                )
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            throw new RuntimeException("No response from Face++ search");
        }

        return parseFaceSearchResult(response);
    }

    private FaceSearchResult parseFaceSearchResult(Map response) {
        try {
            List<Map> results = (List<Map>) response.get("results");

            if (results == null || results.isEmpty()) {
                return new FaceSearchResult(null, 0.0);
            }

            Map topResult = results.get(0);
            String faceToken = topResult.get("face_token").toString();
            double confidence = Double.parseDouble(topResult.get("confidence").toString());

            return new FaceSearchResult(faceToken, confidence);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Face++ response: " + e.getMessage());
        }
    }

    public static class FaceSearchResult {
        public final String faceToken;
        public final double confidence;

        public FaceSearchResult(String faceToken, double confidence) {
            this.faceToken = faceToken;
            this.confidence = confidence;
        }
    }
}
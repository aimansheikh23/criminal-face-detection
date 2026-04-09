package com.criminaldetection.criminal_face_detection.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "criminal_photo")
@Setter
@Getter
public class CriminalPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "criminal_id")
    private Criminal criminal;

    @Column(name = "minio_object_key")
    private String minioObjectKey;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(columnDefinition = "TEXT")
    private String embedding;

    public CriminalPhoto(){}

    public CriminalPhoto(Criminal criminal,  String minio_object_key, String image_url, LocalDateTime updated_at,  String embedding){
        this.criminal = criminal;
        this.minioObjectKey = minio_object_key;
        this.imageUrl = image_url;
        this.uploadedAt = updated_at;
        this.embedding = embedding;
    }
}

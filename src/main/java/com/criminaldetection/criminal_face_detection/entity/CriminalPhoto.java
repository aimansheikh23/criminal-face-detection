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


    @Column(name = "face_token")
    private String faceToken;

    public CriminalPhoto(){}

    public CriminalPhoto(Criminal criminal,  String minio_object_key, String  face_token){
        this.criminal = criminal;
        this.minioObjectKey = minio_object_key;
        this.faceToken = face_token;
    }
}

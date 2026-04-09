package com.criminaldetection.criminal_face_detection.service;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class MinIoService {
    @Value("${minio.bucket}")
    private String bucket;

    @Autowired
    private MinioClient minioClient;

    public String uploadImage(MultipartFile file) {
        try {
            //generated file name uniquely
            String objectKey = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            //check bucket list exists - if not create it
            boolean bucketListExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!bucketListExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                    .build());
            return objectKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to MinIO: " + e.getMessage());
        }

    }

    public String getImageUrl(String objectKey){
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(1, TimeUnit.HOURS)
                    .method(Method.GET)
                    .build());
        }catch (Exception e){
            throw new RuntimeException("Failed to get image URL: " + e.getMessage());
        }
    }

    public void  deleteImage(String objectKey){
        try {
              minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                    .build());
        }catch (Exception e){
            throw new RuntimeException("Failed to delete image from MinIO: " + e.getMessage());
        }

    }

}

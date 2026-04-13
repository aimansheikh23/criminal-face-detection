package com.criminaldetection.criminal_face_detection.repository;

import com.criminaldetection.criminal_face_detection.entity.CriminalPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriminalPhotoRepository extends JpaRepository<CriminalPhoto, Integer> {

    List<CriminalPhoto> findAllByCriminalId(Integer criminaId);
    Optional<CriminalPhoto> findByFaceToken(String faceToken);
    Optional<CriminalPhoto> findByCriminalId(Integer criminaId);
}

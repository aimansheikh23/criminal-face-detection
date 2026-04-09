package com.criminaldetection.criminal_face_detection.repository;

import com.criminaldetection.criminal_face_detection.entity.Criminal;
import com.criminaldetection.criminal_face_detection.enums.CrimeType;
import com.criminaldetection.criminal_face_detection.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriminalRepository extends JpaRepository<Criminal, Integer> {

    List<Criminal> findByStatus(Status status);

    List<Criminal> findByCrimeType(CrimeType  type);

    Optional<Criminal> findByFullNameIgnoreCase(String fullName);
}

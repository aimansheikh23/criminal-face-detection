package com.criminaldetection.criminal_face_detection.repository;

import com.criminaldetection.criminal_face_detection.entity.DetectionLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetectionLogRepository extends JpaRepository<DetectionLogs, Integer> {
    List<DetectionLogs> findByOperatorId(Integer operatorId);
    List<DetectionLogs> findByIsMatched(boolean isMatched);
}

package com.criminaldetection.criminal_face_detection.repository;

import com.criminaldetection.criminal_face_detection.entity.Alerts;
import com.criminaldetection.criminal_face_detection.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alerts, Integer> {
    List<Alerts> findByAlertStatus(AlertStatus status);

    List<Alerts> findByCriminalId(Integer criminaId);
}

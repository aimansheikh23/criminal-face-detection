package com.criminaldetection.criminal_face_detection.service;

import com.criminaldetection.criminal_face_detection.dto.AlertResponse;
import com.criminaldetection.criminal_face_detection.entity.Alerts;
import com.criminaldetection.criminal_face_detection.entity.Criminal;
import com.criminaldetection.criminal_face_detection.entity.DetectionLogs;
import com.criminaldetection.criminal_face_detection.enums.AlertStatus;
import com.criminaldetection.criminal_face_detection.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void createAlert(DetectionLogs detectionLogs, Criminal criminal, double confidenceScore){
        Alerts alerts = new Alerts();
        alerts.setCriminal(criminal);
        alerts.setDetectionLog(detectionLogs);
        alerts.setAlertStatus(AlertStatus.PENDING);
        alerts.setNotifiedVia("WEBSOCKET");
        alerts.setTriggeredAt(LocalDateTime.now());
        alertRepository.save(alerts);

        sendWebSocketAlert(criminal, confidenceScore);
    }

    public void sendWebSocketAlert(Criminal criminal, double confidenceScore){
        AlertResponse alertResponse = new AlertResponse(
                criminal.getFullName(),
                criminal.getCrimeType(),
                confidenceScore,
                "Match Found - Immediate attention required",
                LocalDateTime.now()
        );

        simpMessagingTemplate.convertAndSend("/topic/alerts", alertResponse);
    }
}

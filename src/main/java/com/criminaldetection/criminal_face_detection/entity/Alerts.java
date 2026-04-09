package com.criminaldetection.criminal_face_detection.entity;

import com.criminaldetection.criminal_face_detection.enums.AlertStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
public class Alerts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "detection_log_id", nullable = false)
    private DetectionLogs detectionLog;

    @ManyToOne
    @JoinColumn(name = "criminal_id")
    private Criminal criminal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus alertStatus;

    private String notifiedVia;

    private LocalDateTime triggeredAt;

    public Alerts() {}

    public Alerts(DetectionLogs detectionLog, Criminal criminal, AlertStatus alertStatus, String notifiedVia, LocalDateTime triggeredAt) {
        this.detectionLog = detectionLog;
        this.criminal = criminal;
        this.alertStatus = alertStatus;
        this.notifiedVia = notifiedVia;
        this.triggeredAt = triggeredAt;
    }
}
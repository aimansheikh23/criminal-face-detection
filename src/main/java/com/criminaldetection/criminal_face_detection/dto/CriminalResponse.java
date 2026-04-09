package com.criminaldetection.criminal_face_detection.dto;


import com.criminaldetection.criminal_face_detection.enums.CrimeType;
import com.criminaldetection.criminal_face_detection.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CriminalResponse {
    private String name;
    private CrimeType crimeType;
    private Status status;
    private String alias;
}

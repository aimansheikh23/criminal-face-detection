package com.criminaldetection.criminal_face_detection.entity;

import com.criminaldetection.criminal_face_detection.enums.CrimeType;
import com.criminaldetection.criminal_face_detection.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "criminal")
@Setter
@Getter
public class Criminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
    private String fullName;

    
    private String alias;

    
    private Date dob;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CrimeType crimeType;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    
    private String description;


    public Criminal(){}

    public Criminal(String full_name, String  alias, Date dob, CrimeType crimeType, Status status, String description){
        this.fullName = full_name;
        this.alias = alias;
        this.dob = dob;
        this.crimeType = crimeType;
        this.status = status;
        this.description = description;
    }


}

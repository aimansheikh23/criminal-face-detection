package com.criminaldetection.criminal_face_detection.entity;

import com.criminaldetection.criminal_face_detection.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "user")
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
    private String username;

    
    private String email;

    
    private String password;

    
    private Boolean isActive;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    
    private Date created_at;

    public User(){}

    public User(String username, String email, String password, Role role, Boolean is_active, Date created_at) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = is_active;
        this.created_at = created_at;
    }
}

package com.criminaldetection.criminal_face_detection.controller;

import com.criminaldetection.criminal_face_detection.dto.LoginRequest;
import com.criminaldetection.criminal_face_detection.dto.LoginResponse;
import com.criminaldetection.criminal_face_detection.dto.RegisterRequest;
import com.criminaldetection.criminal_face_detection.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }
}

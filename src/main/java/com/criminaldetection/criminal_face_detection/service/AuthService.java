package com.criminaldetection.criminal_face_detection.service;

import com.criminaldetection.criminal_face_detection.dto.LoginRequest;
import com.criminaldetection.criminal_face_detection.dto.LoginResponse;
import com.criminaldetection.criminal_face_detection.dto.RegisterRequest;
import com.criminaldetection.criminal_face_detection.entity.User;
import com.criminaldetection.criminal_face_detection.enums.Role;
import com.criminaldetection.criminal_face_detection.repository.UserRepository;
import com.criminaldetection.criminal_face_detection.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    public String register(RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole()));
        user.setIs_active(true);

        userRepository.save(user);

        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("Username not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return new LoginResponse(token, user.getUsername(),  user.getRole().toString());
    }

}

package com.spring.auth_service.service;


import com.spring.auth_service.dto.AuthRequest;
import com.spring.auth_service.dto.ForgotPasswordRequest;
import com.spring.auth_service.dto.RegisterRequest;
import com.spring.auth_service.entity.User;
import com.spring.auth_service.repository.UserRepository;
import com.spring.auth_service.util.JwtUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    // ... other fields and constructor ...
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
    }

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent() ||
                userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Email or phone already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    public String login(AuthRequest request) {
        logger.info("Login attempt with username: {}", request.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            logger.info("Authentication successful for: {}", request.getUsername());
            return jwtUtil.generateToken(request.getUsername());
        } catch (AuthenticationException e) {
            logger.error("Authentication failed: {}", e.getMessage());
            throw new RuntimeException("Invalid credentials", e);
        }
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        logger.info("Forgot password request for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("Password Reset");
        message.setText("Your new password is: " + newPassword);
        try {
            mailSender.send(message);
            logger.info("Password reset email sent to: {}", request.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send password reset email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }

        return "New password sent to email";
    }

    private String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }
}
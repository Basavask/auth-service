package com.spring.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    private String username; // Email or phone
    @NotBlank
    private String password;
}
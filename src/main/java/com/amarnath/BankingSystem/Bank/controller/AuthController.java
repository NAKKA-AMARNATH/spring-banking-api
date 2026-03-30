package com.amarnath.BankingSystem.Bank.controller;

import com.amarnath.BankingSystem.Bank.dto.request.LoginRequest;
import com.amarnath.BankingSystem.Bank.dto.request.RegisterRequest;
import com.amarnath.BankingSystem.Bank.dto.response.ApiResponse;
import com.amarnath.BankingSystem.Bank.dto.response.AuthResponse;
import com.amarnath.BankingSystem.Bank.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authService.refreshToken(refreshToken)));
    }
}
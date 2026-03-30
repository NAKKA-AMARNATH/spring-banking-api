package com.amarnath.BankingSystem.Bank.service;

import com.amarnath.BankingSystem.Bank.dto.request.LoginRequest;
import com.amarnath.BankingSystem.Bank.dto.request.RegisterRequest;
import com.amarnath.BankingSystem.Bank.dto.response.AuthResponse;
import com.amarnath.BankingSystem.Bank.entity.User;
import com.amarnath.BankingSystem.Bank.exception.ResourceNotFoundException;
import com.amarnath.BankingSystem.Bank.mapper.UserMapper;
import com.amarnath.BankingSystem.Bank.repository.UserRepository;
import com.amarnath.BankingSystem.Bank.security.CustomUserDetailsService;
import com.amarnath.BankingSystem.Bank.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (userRepository.existsByNationalId(request.getNationalId())) {
            throw new IllegalArgumentException("National ID is already registered");
        }

        User user = UserMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        auditService.log(user.getId(), "REGISTER", "User", user.getId(), "New user registered");

        return UserMapper.toAuthResponse(user, accessToken, refreshToken, jwtTokenProvider.getAccessTokenExpiry());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        auditService.log(user.getId(), "LOGIN", "User", user.getId(), "User logged in");

        return UserMapper.toAuthResponse(user, accessToken, refreshToken, jwtTokenProvider.getAccessTokenExpiry());
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtTokenProvider.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtTokenProvider.isValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return UserMapper.toAuthResponse(user, newAccessToken, newRefreshToken, jwtTokenProvider.getAccessTokenExpiry());
    }
}

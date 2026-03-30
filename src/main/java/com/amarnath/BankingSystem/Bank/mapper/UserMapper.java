package com.amarnath.BankingSystem.Bank.mapper;

import com.amarnath.BankingSystem.Bank.dto.request.RegisterRequest;
import com.amarnath.BankingSystem.Bank.dto.response.AuthResponse;
import com.amarnath.BankingSystem.Bank.entity.User;
import com.amarnath.BankingSystem.Bank.enums.Role;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toEntity(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .phone(request.getPhone())
                .nationalId(request.getNationalId())
                .role(Role.CUSTOMER)
                .status("ACTIVE")
                .build();
    }

    public static AuthResponse toAuthResponse(User user, String accessToken, String refreshToken, long expiresIn) {
        return AuthResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .build();
    }
}

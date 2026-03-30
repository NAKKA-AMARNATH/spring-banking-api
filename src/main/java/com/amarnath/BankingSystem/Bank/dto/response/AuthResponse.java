package com.amarnath.BankingSystem.Bank.dto.response;

import com.amarnath.BankingSystem.Bank.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Long userId;
    private String fullName;
    private String email;
    private Role role;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
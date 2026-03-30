package com.amarnath.BankingSystem.Bank.dto.response;

import com.amarnath.BankingSystem.Bank.enums.AccountStatus;
import com.amarnath.BankingSystem.Bank.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
    private String ownerName;
    private String ownerEmail;
    private LocalDateTime createdAt;
}
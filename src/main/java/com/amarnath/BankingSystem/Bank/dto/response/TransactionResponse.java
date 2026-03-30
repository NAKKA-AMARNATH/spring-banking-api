package com.amarnath.BankingSystem.Bank.dto.response;

import com.amarnath.BankingSystem.Bank.enums.TransactionStatus;
import com.amarnath.BankingSystem.Bank.enums.TransactionType;
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
public class TransactionResponse {

    private Long id;
    private String referenceNumber;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String currency;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String description;
    private LocalDateTime createdAt;
}
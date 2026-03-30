package com.amarnath.BankingSystem.Bank.dto.response;

import com.amarnath.BankingSystem.Bank.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {

    private Long id;
    private Long userId;
    private String accountNumber;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal monthlyEmi;
    private LoanStatus status;
    private LocalDate disbursementDate;
    private LocalDate maturityDate;
    private LocalDateTime createdAt;
}
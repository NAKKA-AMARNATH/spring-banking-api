package com.amarnath.BankingSystem.Bank.mapper;

import com.amarnath.BankingSystem.Bank.dto.response.LoanResponse;
import com.amarnath.BankingSystem.Bank.entity.Loan;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanMapper {

    public static LoanResponse toResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUser().getId())
                .accountNumber(loan.getAccount().getAccountNumber())
                .principalAmount(loan.getPrincipalAmount())
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .monthlyEmi(loan.getMonthlyEmi())
                .status(loan.getStatus())
                .disbursementDate(loan.getDisbursementDate())
                .maturityDate(loan.getMaturityDate())
                .createdAt(loan.getCreatedAt())
                .build();
    }

    public static List<LoanResponse> toResponseList(List<Loan> loans) {
        return loans.stream().map(LoanMapper::toResponse).toList();
    }
}
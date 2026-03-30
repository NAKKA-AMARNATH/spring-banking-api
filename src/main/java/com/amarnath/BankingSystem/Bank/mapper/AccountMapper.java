package com.amarnath.BankingSystem.Bank.mapper;

import com.amarnath.BankingSystem.Bank.dto.request.CreateAccountRequest;
import com.amarnath.BankingSystem.Bank.dto.response.AccountResponse;
import com.amarnath.BankingSystem.Bank.entity.Account;
import com.amarnath.BankingSystem.Bank.entity.User;
import com.amarnath.BankingSystem.Bank.enums.AccountStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountMapper {

    public static Account toEntity(CreateAccountRequest request, User user, String accountNumber) {
        return Account.builder()
                .user(user)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .currency(request.getCurrency())
                .status(AccountStatus.ACTIVE)
                .build();
    }

    public static AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .ownerName(account.getUser().getFullName())
                .ownerEmail(account.getUser().getEmail())
                .createdAt(account.getCreatedAt())
                .build();
    }

    public static List<AccountResponse> toResponseList(List<Account> accounts) {
        return accounts.stream().map(AccountMapper::toResponse).toList();
    }
}
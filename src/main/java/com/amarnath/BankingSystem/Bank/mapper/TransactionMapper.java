package com.amarnath.BankingSystem.Bank.mapper;

import com.amarnath.BankingSystem.Bank.dto.response.TransactionResponse;
import com.amarnath.BankingSystem.Bank.entity.Account;
import com.amarnath.BankingSystem.Bank.entity.Transaction;
import com.amarnath.BankingSystem.Bank.enums.TransactionStatus;
import com.amarnath.BankingSystem.Bank.enums.TransactionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionMapper {

    public static Transaction toDepositEntity(Account toAccount, BigDecimal amount, String referenceNumber, String description) {
        return Transaction.builder()
                .toAccount(toAccount)
                .amount(amount)
                .currency(toAccount.getCurrency())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .referenceNumber(referenceNumber)
                .description(description)
                .build();
    }

    public static Transaction toWithdrawalEntity(Account fromAccount, BigDecimal amount, String referenceNumber, String description) {
        return Transaction.builder()
                .fromAccount(fromAccount)
                .amount(amount)
                .currency(fromAccount.getCurrency())
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.PENDING)
                .referenceNumber(referenceNumber)
                .description(description)
                .build();
    }

    public static Transaction toTransferEntity(Account fromAccount, Account toAccount, BigDecimal amount, String referenceNumber, String description) {
        return Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .currency(fromAccount.getCurrency())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .referenceNumber(referenceNumber)
                .description(description)
                .build();
    }

    public static TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .referenceNumber(transaction.getReferenceNumber())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .fromAccountNumber(transaction.getFromAccount() != null
                        ? transaction.getFromAccount().getAccountNumber() : null)
                .toAccountNumber(transaction.getToAccount() != null
                        ? transaction.getToAccount().getAccountNumber() : null)
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public static List<TransactionResponse> toResponseList(List<Transaction> transactions) {
        return transactions.stream().map(TransactionMapper::toResponse).toList();
    }
}
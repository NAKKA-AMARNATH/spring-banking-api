package com.amarnath.BankingSystem.Bank.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountNumber, BigDecimal available, BigDecimal required) {
        super("Insufficient funds in account " + accountNumber
                + ": available " + available + ", required " + required);
    }
}
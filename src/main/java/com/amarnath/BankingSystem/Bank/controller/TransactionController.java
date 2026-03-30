package com.amarnath.BankingSystem.Bank.controller;

import com.amarnath.BankingSystem.Bank.dto.request.DepositRequest;
import com.amarnath.BankingSystem.Bank.dto.request.TransferRequest;
import com.amarnath.BankingSystem.Bank.dto.request.WithdrawRequest;
import com.amarnath.BankingSystem.Bank.dto.response.ApiResponse;
import com.amarnath.BankingSystem.Bank.dto.response.TransactionResponse;
import com.amarnath.BankingSystem.Bank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit/{accountId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody DepositRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit successful",
                        transactionService.deposit(accountId, request, userDetails.getUsername())));
    }

    @PostMapping("/withdraw/{accountId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody WithdrawRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Withdrawal successful",
                        transactionService.withdraw(accountId, request, userDetails.getUsername())));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer successful",
                        transactionService.transfer(request, userDetails.getUsername())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved",
                transactionService.getById(id, userDetails.getUsername())));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAccountTransactions(
            @PathVariable Long accountId,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved",
                transactionService.getAccountTransactions(accountId, userDetails.getUsername(), pageable)));
    }
}
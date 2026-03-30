package com.amarnath.BankingSystem.Bank.controller;

import com.amarnath.BankingSystem.Bank.dto.response.AccountResponse;
import com.amarnath.BankingSystem.Bank.dto.response.ApiResponse;
import com.amarnath.BankingSystem.Bank.dto.response.LoanResponse;
import com.amarnath.BankingSystem.Bank.service.ManagerService;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@Validated
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/loans/pending")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getPendingLoans() {
        return ResponseEntity.ok(ApiResponse.success("Pending loans retrieved",
                managerService.getPendingLoans()));
    }

    @PutMapping("/loans/{id}/approve")
    public ResponseEntity<ApiResponse<LoanResponse>> approveLoan(
            @PathVariable Long id,
            @RequestParam @DecimalMin("0.01") BigDecimal interestRate,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Loan approved",
                managerService.approveLoan(id, interestRate, userDetails.getUsername())));
    }

    @PutMapping("/loans/{id}/reject")
    public ResponseEntity<ApiResponse<LoanResponse>> rejectLoan(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Loan rejected",
                managerService.rejectLoan(id, userDetails.getUsername())));
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts() {
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved",
                managerService.getAllAccounts()));
    }

    @PutMapping("/accounts/{id}/freeze")
    public ResponseEntity<ApiResponse<AccountResponse>> freezeAccount(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Account frozen",
                managerService.freezeAccount(id, userDetails.getUsername())));
    }

    @PutMapping("/accounts/{id}/unfreeze")
    public ResponseEntity<ApiResponse<AccountResponse>> unfreezeAccount(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Account unfrozen",
                managerService.unfreezeAccount(id, userDetails.getUsername())));
    }
}
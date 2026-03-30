package com.amarnath.BankingSystem.Bank.controller;

import com.amarnath.BankingSystem.Bank.dto.request.LoanApplicationRequest;
import com.amarnath.BankingSystem.Bank.dto.response.ApiResponse;
import com.amarnath.BankingSystem.Bank.dto.response.LoanResponse;
import com.amarnath.BankingSystem.Bank.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoanResponse>> apply(
            @Valid @RequestBody LoanApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Loan application submitted",
                        loanService.applyForLoan(request, userDetails.getUsername())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getMyLoans(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved",
                loanService.getMyLoans(userDetails.getUsername())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Loan retrieved",
                loanService.getLoanById(id, userDetails.getUsername())));
    }
}
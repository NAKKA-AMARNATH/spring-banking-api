package com.amarnath.BankingSystem.Bank.service;

import com.amarnath.BankingSystem.Bank.dto.request.LoanApplicationRequest;
import com.amarnath.BankingSystem.Bank.dto.response.LoanResponse;
import com.amarnath.BankingSystem.Bank.entity.Account;
import com.amarnath.BankingSystem.Bank.entity.Loan;
import com.amarnath.BankingSystem.Bank.entity.User;
import com.amarnath.BankingSystem.Bank.enums.AccountStatus;
import com.amarnath.BankingSystem.Bank.enums.LoanStatus;
import com.amarnath.BankingSystem.Bank.enums.Role;
import com.amarnath.BankingSystem.Bank.exception.ResourceNotFoundException;
import com.amarnath.BankingSystem.Bank.mapper.LoanMapper;
import com.amarnath.BankingSystem.Bank.repository.AccountRepository;
import com.amarnath.BankingSystem.Bank.repository.LoanRepository;
import com.amarnath.BankingSystem.Bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final BigDecimal DEFAULT_ANNUAL_RATE = new BigDecimal("12.00");

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    public LoanResponse applyForLoan(LoanApplicationRequest request, String email) {
        User user = findUserByEmail(email);

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", request.getAccountId()));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Account does not belong to you");
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account must be active to apply for a loan");
        }

        BigDecimal emi = calculateEmi(request.getPrincipalAmount(), DEFAULT_ANNUAL_RATE, request.getTenureMonths());

        Loan loan = Loan.builder()
                .user(user)
                .account(account)
                .principalAmount(request.getPrincipalAmount())
                .interestRate(DEFAULT_ANNUAL_RATE)
                .tenureMonths(request.getTenureMonths())
                .monthlyEmi(emi)
                .status(LoanStatus.APPLIED)
                .build();

        loan = loanRepository.save(loan);

        auditService.log(user.getId(), "LOAN_APPLY", "Loan", loan.getId(),
                "Applied for loan of " + request.getPrincipalAmount());

        return LoanMapper.toResponse(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getMyLoans(String email) {
        User user = findUserByEmail(email);
        return LoanMapper.toResponseList(loanRepository.findByUserId(user.getId()));
    }

    @Transactional(readOnly = true)
    public LoanResponse getLoanById(Long id, String email) {
        Loan loan = findLoanById(id);
        User requester = findUserByEmail(email);

        if (requester.getRole() == Role.CUSTOMER && !loan.getUser().getId().equals(requester.getId())) {
            throw new AccessDeniedException("You do not have access to this loan");
        }

        return LoanMapper.toResponse(loan);
    }

    Loan findLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", id));
    }

    User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    static BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, int months) {
        // EMI = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal r = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusRPowN = BigDecimal.ONE.add(r).pow(months);
        return principal.multiply(r).multiply(onePlusRPowN)
                .divide(onePlusRPowN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }
}
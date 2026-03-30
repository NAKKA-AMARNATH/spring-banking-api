package com.amarnath.BankingSystem.Bank.service;

import com.amarnath.BankingSystem.Bank.dto.response.AccountResponse;
import com.amarnath.BankingSystem.Bank.dto.response.LoanResponse;
import com.amarnath.BankingSystem.Bank.entity.Account;
import com.amarnath.BankingSystem.Bank.entity.Loan;
import com.amarnath.BankingSystem.Bank.entity.User;
import com.amarnath.BankingSystem.Bank.enums.AccountStatus;
import com.amarnath.BankingSystem.Bank.enums.LoanStatus;
import com.amarnath.BankingSystem.Bank.exception.ResourceNotFoundException;
import com.amarnath.BankingSystem.Bank.mapper.AccountMapper;
import com.amarnath.BankingSystem.Bank.mapper.LoanMapper;
import com.amarnath.BankingSystem.Bank.repository.AccountRepository;
import com.amarnath.BankingSystem.Bank.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final LoanService loanService;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<LoanResponse> getPendingLoans() {
        return LoanMapper.toResponseList(loanRepository.findPendingApplications());
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return AccountMapper.toResponseList(accountRepository.findAll());
    }

    @Transactional
    public LoanResponse approveLoan(Long loanId, BigDecimal interestRate, String managerEmail) {
        Loan loan = loanService.findLoanById(loanId);

        if (loan.getStatus() != LoanStatus.APPLIED) {
            throw new IllegalStateException("Only loans in APPLIED status can be approved");
        }

        BigDecimal emi = LoanService.calculateEmi(loan.getPrincipalAmount(), interestRate, loan.getTenureMonths());
        LocalDate disbursementDate = LocalDate.now();
        LocalDate maturityDate = disbursementDate.plusMonths(loan.getTenureMonths());

        loan.setStatus(LoanStatus.APPROVED);
        loan.setInterestRate(interestRate);
        loan.setMonthlyEmi(emi);
        loan.setDisbursementDate(disbursementDate);
        loan.setMaturityDate(maturityDate);
        loanRepository.save(loan);

        User manager = loanService.findUserByEmail(managerEmail);
        auditService.log(manager.getId(), "LOAN_APPROVE", "Loan", loanId,
                "Approved at " + interestRate + "% by " + managerEmail);

        return LoanMapper.toResponse(loan);
    }

    @Transactional
    public LoanResponse rejectLoan(Long loanId, String managerEmail) {
        Loan loan = loanService.findLoanById(loanId);

        if (loan.getStatus() != LoanStatus.APPLIED) {
            throw new IllegalStateException("Only loans in APPLIED status can be rejected");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loanRepository.save(loan);

        User manager = loanService.findUserByEmail(managerEmail);
        auditService.log(manager.getId(), "LOAN_REJECT", "Loan", loanId,
                "Rejected by " + managerEmail);

        return LoanMapper.toResponse(loan);
    }

    @Transactional
    public AccountResponse freezeAccount(Long accountId, String managerEmail) {
        Account account = findAccountById(accountId);

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new IllegalStateException("Account is already frozen");
        }
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot freeze a closed account");
        }

        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);

        User manager = loanService.findUserByEmail(managerEmail);
        auditService.log(manager.getId(), "FREEZE_ACCOUNT", "Account", accountId,
                "Frozen by " + managerEmail);

        return AccountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse unfreezeAccount(Long accountId, String managerEmail) {
        Account account = findAccountById(accountId);

        if (account.getStatus() != AccountStatus.FROZEN) {
            throw new IllegalStateException("Account is not frozen");
        }

        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);

        User manager = loanService.findUserByEmail(managerEmail);
        auditService.log(manager.getId(), "UNFREEZE_ACCOUNT", "Account", accountId,
                "Unfrozen by " + managerEmail);

        return AccountMapper.toResponse(account);
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }
}
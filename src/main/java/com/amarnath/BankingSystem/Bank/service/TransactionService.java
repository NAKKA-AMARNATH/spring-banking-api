package com.amarnath.BankingSystem.Bank.service;

import com.amarnath.BankingSystem.Bank.dto.request.DepositRequest;
import com.amarnath.BankingSystem.Bank.dto.request.TransferRequest;
import com.amarnath.BankingSystem.Bank.dto.request.WithdrawRequest;
import com.amarnath.BankingSystem.Bank.dto.response.TransactionResponse;
import com.amarnath.BankingSystem.Bank.entity.Account;
import com.amarnath.BankingSystem.Bank.entity.Transaction;
import com.amarnath.BankingSystem.Bank.entity.User;
import com.amarnath.BankingSystem.Bank.enums.AccountStatus;
import com.amarnath.BankingSystem.Bank.enums.Role;
import com.amarnath.BankingSystem.Bank.enums.TransactionStatus;
import com.amarnath.BankingSystem.Bank.exception.InsufficientFundsException;
import com.amarnath.BankingSystem.Bank.exception.ResourceNotFoundException;
import com.amarnath.BankingSystem.Bank.mapper.TransactionMapper;
import com.amarnath.BankingSystem.Bank.repository.AccountRepository;
import com.amarnath.BankingSystem.Bank.repository.TransactionRepository;
import com.amarnath.BankingSystem.Bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    public TransactionResponse deposit(Long accountId, DepositRequest request, String email) {
        Account account = getOwnedActiveAccount(accountId, email);

        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        Transaction transaction = transactionRepository.save(
                TransactionMapper.toDepositEntity(account, request.amount(), generateRef(), request.description())
                        .toBuilder().status(TransactionStatus.SUCCESS).build()
        );

        auditService.log(account.getUser().getId(), "DEPOSIT", "Transaction", transaction.getId(),
                "Deposited " + request.amount() + " to " + account.getAccountNumber());

        return TransactionMapper.toResponse(transaction);
    }

    @Transactional
    public TransactionResponse withdraw(Long accountId, WithdrawRequest request, String email) {
        Account account = getOwnedActiveAccount(accountId, email);

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException(account.getAccountNumber(), account.getBalance(), request.amount());
        }

        account.setBalance(account.getBalance().subtract(request.amount()));
        accountRepository.save(account);

        Transaction transaction = transactionRepository.save(
                TransactionMapper.toWithdrawalEntity(account, request.amount(), generateRef(), request.description())
                        .toBuilder().status(TransactionStatus.SUCCESS).build()
        );

        auditService.log(account.getUser().getId(), "WITHDRAWAL", "Transaction", transaction.getId(),
                "Withdrew " + request.amount() + " from " + account.getAccountNumber());

        return TransactionMapper.toResponse(transaction);
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request, String email) {
        Account from = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "number", request.getFromAccountNumber()));

        if (!from.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Source account does not belong to you");
        }
        if (from.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Source account is not active");
        }
        if (from.getAccountNumber().equals(request.getToAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account to = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "number", request.getToAccountNumber()));

        if (to.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Destination account is not active");
        }
        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(from.getAccountNumber(), from.getBalance(), request.getAmount());
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        Transaction transaction = transactionRepository.save(
                TransactionMapper.toTransferEntity(from, to, request.getAmount(), generateRef(), request.getDescription())
                        .toBuilder().status(TransactionStatus.SUCCESS).build()
        );

        auditService.log(from.getUser().getId(), "TRANSFER", "Transaction", transaction.getId(),
                "Transferred " + request.getAmount() + " from " + from.getAccountNumber()
                        + " to " + to.getAccountNumber());

        return TransactionMapper.toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(Long id, String email) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (requester.getRole() == Role.CUSTOMER && !isParticipant(transaction, requester.getId())) {
            throw new AccessDeniedException("You do not have access to this transaction");
        }

        return TransactionMapper.toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAccountTransactions(Long accountId, String email, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (requester.getRole() == Role.CUSTOMER && !account.getUser().getId().equals(requester.getId())) {
            throw new AccessDeniedException("You do not have access to this account");
        }

        return transactionRepository.findByAccountId(accountId, pageable)
                .map(TransactionMapper::toResponse);
    }

    private Account getOwnedActiveAccount(Long accountId, String email) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        if (!account.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Account does not belong to you");
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        return account;
    }

    private boolean isParticipant(Transaction t, Long userId) {
        return (t.getFromAccount() != null && t.getFromAccount().getUser().getId().equals(userId))
                || (t.getToAccount() != null && t.getToAccount().getUser().getId().equals(userId));
    }

    private String generateRef() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 13).toUpperCase();
    }
}

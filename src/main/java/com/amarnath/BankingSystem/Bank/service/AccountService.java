package com.amarnath.BankingSystem.Bank.service;

import com.amarnath.BankingSystem.Bank.dto.request.CreateAccountRequest;
import com.amarnath.BankingSystem.Bank.dto.response.AccountResponse;
import com.amarnath.BankingSystem.Bank.entity.Account;
import com.amarnath.BankingSystem.Bank.entity.User;
import com.amarnath.BankingSystem.Bank.enums.Role;
import com.amarnath.BankingSystem.Bank.exception.ResourceNotFoundException;
import com.amarnath.BankingSystem.Bank.mapper.AccountMapper;
import com.amarnath.BankingSystem.Bank.repository.AccountRepository;
import com.amarnath.BankingSystem.Bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, String email) {
        User user = findUserByEmail(email);

        String accountNumber = generateUniqueAccountNumber();
        Account account = AccountMapper.toEntity(request, user, accountNumber);
        account = accountRepository.save(account);

        auditService.log(user.getId(), "CREATE_ACCOUNT", "Account", account.getId(),
                "Created " + request.getAccountType() + " account: " + accountNumber);

        return AccountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts(String email) {
        User user = findUserByEmail(email);
        return AccountMapper.toResponseList(accountRepository.findByUserId(user.getId()));
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id, String email) {
        Account account = findAccountById(id);
        User requester = findUserByEmail(email);

        if (requester.getRole() == Role.CUSTOMER && !account.getUser().getId().equals(requester.getId())) {
            throw new AccessDeniedException("You do not have access to this account");
        }

        return AccountMapper.toResponse(account);
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private String generateUniqueAccountNumber() {
        String number;
        do {
            number = "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
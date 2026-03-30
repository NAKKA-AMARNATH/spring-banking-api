package com.amarnath.BankingSystem.Bank.repository;

import com.amarnath.BankingSystem.Bank.entity.Account;
import com.amarnath.BankingSystem.Bank.enums.AccountStatus;
import com.amarnath.BankingSystem.Bank.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByUserId(Long userId);

    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);

    List<Account> findByUserIdAndAccountType(Long userId, AccountType accountType);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountType = :type AND a.status = :status")
    Optional<Account> findByUserIdAndAccountTypeAndStatus(
            @Param("userId") Long userId,
            @Param("type") AccountType type,
            @Param("status") AccountStatus status);

    List<Account> findByStatus(AccountStatus status);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}

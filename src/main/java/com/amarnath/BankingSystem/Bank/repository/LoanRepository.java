package com.amarnath.BankingSystem.Bank.repository;

import com.amarnath.BankingSystem.Bank.entity.Loan;
import com.amarnath.BankingSystem.Bank.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);

    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByAccountId(Long accountId);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status NOT IN ('REJECTED', 'CLOSED')")
    long countActiveByUserId(@Param("userId") Long userId);

    @Query("SELECT l FROM Loan l WHERE l.status = 'APPLIED' ORDER BY l.createdAt ASC")
    List<Loan> findPendingApplications();
}
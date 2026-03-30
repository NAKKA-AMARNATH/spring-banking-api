package com.amarnath.BankingSystem.Bank.service;

import com.amarnath.BankingSystem.Bank.entity.AuditLog;
import com.amarnath.BankingSystem.Bank.entity.User;
import com.amarnath.BankingSystem.Bank.repository.AuditLogRepository;
import com.amarnath.BankingSystem.Bank.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Async
    public void log(Long userId, String action, String entityType, Long entityId, String details) {
        User user = (userId != null)
                ? userRepository.findById(userId).orElse(null)
                : null;

        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .ipAddress(resolveClientIp())
                .details(details)
                .build();

        auditLogRepository.save(log);
    }

    private String resolveClientIp() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            return (forwarded != null) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}

package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.AuditLog;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AuditLogService {
    AuditLog createAuditLog(AuditLog log);
    Optional<AuditLog> getById(Long id);
    List<AuditLog> getAll();
}

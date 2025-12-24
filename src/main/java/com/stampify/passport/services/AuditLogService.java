package com.stampify.passport.services;

import com.stampify.passport.models.AuditLog;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditLogService {

    public AuditLog createAuditLog(AuditLog log) {
        return null;
    }

    public Optional<AuditLog> getById(Long id) {
        return Optional.empty();
    }

    public List<AuditLog> getAll() {
        return List.of();
    }
}

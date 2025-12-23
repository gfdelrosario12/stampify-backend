package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.AuditPasswordChange;
import org.springframework.stereotype.Service;

@Service
public interface AuditPasswordChangeService {
    AuditPasswordChange create(AuditPasswordChange change);
}

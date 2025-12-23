package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.AuditStampAction;
import org.springframework.stereotype.Service;

@Service
public interface AuditStampActionService {
    AuditStampAction create(AuditStampAction action);
}

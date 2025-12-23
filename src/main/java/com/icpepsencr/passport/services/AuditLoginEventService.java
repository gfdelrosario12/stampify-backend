package com.icpepsencr.passport.services;


import com.icpepsencr.passport.models.AuditLoginEvent;
import org.springframework.stereotype.Service;

@Service
public interface AuditLoginEventService {
    AuditLoginEvent create(AuditLoginEvent event);
}

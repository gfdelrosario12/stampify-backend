package com.stampify.passport.repositories;

import com.stampify.passport.models.OrganizationAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationAuditLogRepository extends JpaRepository<OrganizationAuditLog, Long> {
    // You can add custom queries here if needed later
}

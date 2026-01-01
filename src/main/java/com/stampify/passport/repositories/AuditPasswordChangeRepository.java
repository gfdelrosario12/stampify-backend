package com.stampify.passport.repositories;

import com.stampify.passport.models.AuditPasswordChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditPasswordChangeRepository
        extends JpaRepository<AuditPasswordChange, Long> {
}

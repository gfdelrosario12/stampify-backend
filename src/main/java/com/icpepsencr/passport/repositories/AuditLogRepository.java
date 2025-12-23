package com.icpepsencr.passport.repositories;
import com.icpepsencr.passport.models.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> { }
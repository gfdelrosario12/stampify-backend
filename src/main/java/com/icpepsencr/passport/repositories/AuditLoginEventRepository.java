package com.icpepsencr.passport.repositories;
import com.icpepsencr.passport.models.AuditLoginEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AuditLoginEventRepository extends JpaRepository<AuditLoginEvent, Long> { }
package com.icpepsencr.passport.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AuditPasswordChangeRepository extends JpaRepository<AuditPasswordChange, Long> { }
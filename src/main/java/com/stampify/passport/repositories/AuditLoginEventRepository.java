package com.stampify.passport.repositories;
import com.stampify.passport.models.AuditLoginEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLoginEventRepository extends JpaRepository<AuditLoginEvent, Long> { }
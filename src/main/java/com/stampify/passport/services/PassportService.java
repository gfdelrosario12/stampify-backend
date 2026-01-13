package com.stampify.passport.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stampify.passport.models.*;
import com.stampify.passport.repositories.AuditLogRepository;
import com.stampify.passport.repositories.PassportRepository;
import com.stampify.passport.repositories.StampRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PassportService {

    private final PassportRepository passportRepository;
    private final StampRepository stampRepository;
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PassportService(PassportRepository passportRepository,
                           StampRepository stampRepository,
                           AuditLogRepository auditLogRepository) {
        this.passportRepository = passportRepository;
        this.stampRepository = stampRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /* ======================================================
       LIST / QUERY PASSPORTS
       ====================================================== */
    public List<Passport> getAllPassports() {
        return passportRepository.findAll();
    }

    /* ======================================================
       GET PASSPORT
       ====================================================== */
    public Optional<Passport> getById(Long id) {
        return passportRepository.findById(id);
    }

    public List<Passport> getByMemberId(Long memberId) {
        return passportRepository.findByMemberId(memberId);
    }

    /* ======================================================
       CREATE PASSPORT
       ====================================================== */
    public Passport createPassport(Passport passport, User actorUser) throws Exception {
        passport.setCreatedAt(LocalDateTime.now());
        Passport saved = passportRepository.save(passport);

        logAudit(actorUser, "PASSPORT", "CREATE", saved, null, saved);

        return saved;
    }

    /* ======================================================
       UPDATE PASSPORT
       ====================================================== */
    public Passport updatePassport(Passport updated, User actorUser) throws Exception {
        Passport existing = passportRepository.findById(updated.getId())
                .orElseThrow(() -> new IllegalArgumentException("Passport not found"));

        String previousData = serialize(existing);

        existing.setIssuedAt(updated.getIssuedAt());
        existing.setExpiresAt(updated.getExpiresAt());
        existing.setPassportStatus(updated.getPassportStatus());
        existing.setUpdatedAt(LocalDateTime.now());

        Passport saved = passportRepository.save(existing);

        logAudit(actorUser, "PASSPORT", "UPDATE", saved, previousData, saved);

        return saved;
    }

    /* ======================================================
       SOFT DELETE PASSPORT
       ====================================================== */
    public void deletePassport(Long id, User actorUser) throws Exception {
        Passport passport = passportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Passport not found"));

        String previousData = serialize(passport);

        // Soft delete passport
        passport.setPassportStatus("REVOKED");
        passport.setUpdatedAt(LocalDateTime.now());
        passportRepository.save(passport);

        // Handle orphan stamps
        handleOrphanStamps(passport);

        logAudit(actorUser, "PASSPORT", "DELETE", passport, previousData, null);
    }

    /* ======================================================
       ORPHAN HANDLER
       ====================================================== */
    private void handleOrphanStamps(Passport passport) {
        List<Stamp> stamps = stampRepository.findByPassportAndDeletedAtIsNull(passport);
        LocalDateTime now = LocalDateTime.now();
        for (Stamp stamp : stamps) {
            stamp.setValid(false);
            stamp.setDeletedAt(now);
            stampRepository.save(stamp);
        }
    }

    /* ======================================================
       HELPER METHODS
       ====================================================== */
    private void logAudit(User actorUser, String entityName, String actionName,
                          Object entity, String previousData, Object newData) throws Exception {

        AuditLog auditLog = new AuditLog();
        auditLog.setActorUser(actorUser);

        if (entity instanceof Passport passport) {
            auditLog.setEntityId(passport.getId());
            auditLog.setOrganization(passport.getMember().getOrganization());
        }

        auditLog.setActionCategory(entityName);
        auditLog.setActionName(actionName);
        auditLog.setEntityName(entityName);
        auditLog.setPreviousData(previousData);
        auditLog.setNewData(newData != null ? serialize(newData) : null);
        auditLog.setOccurredAt(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}

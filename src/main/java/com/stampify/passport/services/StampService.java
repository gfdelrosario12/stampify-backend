package com.stampify.passport.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stampify.passport.models.*;
import com.stampify.passport.repositories.AuditLogRepository;
import com.stampify.passport.repositories.StampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StampService {

    @Autowired private StampRepository stampRepository;
    @Autowired private AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create a stamp
     * - Prevents duplicate stamp per passport + event
     * - Auto sets timestamps
     * - Audit log
     */
    public Stamp createStamp(Stamp stamp, User actorUser) throws Exception {

        Optional<Stamp> existingStamp =
                stampRepository.findByPassport_IdAndEvent_Id(
                        stamp.getPassport().getId(),
                        stamp.getEvent().getId()
                );

        if (existingStamp.isPresent()) {
            throw new IllegalStateException("Passport already stamped for this event");
        }

        stamp.setCreatedAt(LocalDateTime.now());
        if (stamp.getStampedAt() == null) {
            stamp.setStampedAt(LocalDateTime.now());
        }
        if (stamp.getScanStatus() == null) {
            stamp.setScanStatus("SUCCESS");
        }

        Stamp saved = stampRepository.save(stamp);

        logAudit(actorUser, "STAMP", "CREATE", saved, null, saved);

        return saved;
    }

    /**
     * Get stamp by ID
     */
    public Optional<Stamp> getById(Long id) {
        return stampRepository.findById(id);
    }

    /**
     * Get stamps by passport
     */
    public List<Stamp> getByPassportId(Long passportId) {
        // Only return stamps that are not revoked
        return stampRepository.findByPassport_IdAndScanStatusNot(passportId, "REVOKED");
    }

    /**
     * Get stamps scanned by a specific scanner
     */
    public List<Stamp> getByScannerId(Long scannerId) {
        return stampRepository.findByScanner_Id(scannerId);
    }

    /**
     * Update stamp
     * - Only allows updating scanStatus or stampedAt
     * - Does NOT allow changing passport, event, or scanner
     * - Audit log
     */
    public Stamp updateStamp(Stamp updatedStamp, User actorUser) throws Exception {

        Stamp existing = stampRepository.findById(updatedStamp.getId())
                .orElseThrow(() -> new IllegalArgumentException("Stamp not found"));

        String previousData = serialize(existing);

        existing.setScanStatus(updatedStamp.getScanStatus());
        existing.setStampedAt(updatedStamp.getStampedAt());
        existing.setCreatedAt(existing.getCreatedAt()); // preserve creation timestamp

        Stamp saved = stampRepository.save(existing);

        logAudit(actorUser, "STAMP", "UPDATE", saved, previousData, saved);

        return saved;
    }

    /**
     * Soft delete stamp
     * - Marks invalid and keeps history
     * - Audit log
     */
    public void deleteStamp(Long id, User actorUser) throws Exception {

        Stamp stamp = stampRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stamp not found"));

        String previousData = serialize(stamp);

        // Soft delete: invalidate
        stamp.setScanStatus("REVOKED");
        stamp.setStampedAt(LocalDateTime.now());
        stampRepository.save(stamp);

        logAudit(actorUser, "STAMP", "DELETE", stamp, previousData, null);
    }

    /* ======================================================
       HELPER METHODS
       ====================================================== */
    private void logAudit(User actorUser, String entityName, String actionName,
                          Object entity, String previousData, Object newData) throws Exception {

        AuditLog auditLog = new AuditLog();
        auditLog.setActorUser(actorUser);

        if (entity instanceof Stamp stamp) {
            auditLog.setEntityId(stamp.getId());
            auditLog.setOrganization(stamp.getPassport().getMember().getOrganization());
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

    public Optional<Stamp> getByPassportAndEvent(Long passportId, Long eventId) {
        return stampRepository.findByPassport_IdAndEvent_Id(passportId, eventId);
    }

}

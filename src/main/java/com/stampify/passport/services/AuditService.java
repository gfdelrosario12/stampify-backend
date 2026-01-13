package com.stampify.passport.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stampify.passport.models.*;
import com.stampify.passport.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLoginEventRepository auditLoginEventRepository;
    private final AuditPasswordChangeRepository auditPasswordChangeRepository;
    private final AuditStampActionRepository auditStampActionRepository;
    private final OrganizationAuditLogRepository organizationAuditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditService(
            AuditLogRepository auditLogRepository,
            AuditLoginEventRepository auditLoginEventRepository,
            AuditPasswordChangeRepository auditPasswordChangeRepository,
            AuditStampActionRepository auditStampActionRepository,
            OrganizationAuditLogRepository organizationAuditLogRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.auditLoginEventRepository = auditLoginEventRepository;
        this.auditPasswordChangeRepository = auditPasswordChangeRepository;
        this.auditStampActionRepository = auditStampActionRepository;
        this.organizationAuditLogRepository = organizationAuditLogRepository;
    }

    /* ================= GENERAL ENTITY AUDIT ================= */
    public AuditLog logEntityAction(User actorUser, Organization org, String entityName,
                                    String actionName, Object entity, Object previousData) {

        AuditLog log = new AuditLog();
        log.setActorUser(actorUser);
        log.setOrganization(org);
        log.setActionCategory(entityName);
        log.setActionName(actionName);
        log.setEntityName(entityName);
        log.setEntityId(getEntityId(entity));
        log.setPreviousData(previousData != null ? serialize(previousData) : null);
        log.setNewData(entity != null ? serialize(entity) : null);
        log.setOccurredAt(LocalDateTime.now());

        return auditLogRepository.save(log);
    }

    /* ================= LOGIN AUDIT ================= */
    public AuditLoginEvent logLogin(User actorUser, boolean isSuccess, String failureReason) {
        AuditLog log = new AuditLog();
        log.setActorUser(actorUser);
        log.setActionCategory("USER");
        log.setActionName("LOGIN");
        log.setEntityName("USER");
        log.setEntityId(actorUser != null ? actorUser.getId() : null);
        log.setOccurredAt(LocalDateTime.now());
        auditLogRepository.save(log);

        AuditLoginEvent event = new AuditLoginEvent();
        event.setAuditLog(log);
        event.setUser(actorUser);
        event.setSuccessful(isSuccess);
        event.setFailureReason(failureReason);
        event.setOccurredAt(LocalDateTime.now());

        return auditLoginEventRepository.save(event);
    }

    /* ================= PASSWORD CHANGE AUDIT ================= */
    public AuditPasswordChange logPasswordChange(User user, User changedBy, Object previousData) {

        AuditLog log = new AuditLog();
        log.setActorUser(changedBy);
        log.setActionCategory("USER");
        log.setActionName("PASSWORD_CHANGE");
        log.setEntityName("USER");
        log.setEntityId(user.getId());
        log.setPreviousData(previousData != null ? serialize(previousData) : null);
        log.setNewData(user != null ? serialize(user) : null);
        log.setOccurredAt(LocalDateTime.now());
        auditLogRepository.save(log);

        AuditPasswordChange pwChange = new AuditPasswordChange();
        pwChange.setAuditLog(log);
        pwChange.setUser(user);
        pwChange.setChangedBy(changedBy);
        pwChange.setChangedAt(LocalDateTime.now());

        return auditPasswordChangeRepository.save(pwChange);
    }

    /* ================= STAMP ACTION AUDIT ================= */
    public AuditStampAction logStampAction(User actorUser, Stamp stamp, String action, OrgScanner scanner) {

        AuditLog log = logEntityAction(actorUser, stamp.getPassport().getMember().getOrganization(),
                "STAMP", action, stamp, null);

        AuditStampAction stampAction = new AuditStampAction();
        stampAction.setAuditLog(log);
        stampAction.setStamp(stamp);
        stampAction.setAction(action);
        stampAction.setPerformedByScanner(scanner);
        stampAction.setPerformedAt(LocalDateTime.now());

        return auditStampActionRepository.save(stampAction);
    }

    /* ================= ORGANIZATION AUDIT ================= */
    public OrganizationAuditLog logOrganizationAction(SuperAdmin actor, Organization org,
                                                      String actionCategory, String actionName) {
        OrganizationAuditLog log = new OrganizationAuditLog();
        log.setActorSuperAdminId(actor.getId());
        log.setOrganization(org);
        log.setActionCategory(actionCategory);
        log.setActionName(actionName);
        log.setEntityName("Organization");

        return organizationAuditLogRepository.save(log);
    }

    /* ================= GET ALL METHODS ================= */
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    public List<AuditLoginEvent> getAllAuditLoginEvents() {
        return auditLoginEventRepository.findAll();
    }

    public List<AuditPasswordChange> getAllAuditPasswordChanges() {
        return auditPasswordChangeRepository.findAll();
    }

    public List<AuditStampAction> getAllAuditStampActions() {
        return auditStampActionRepository.findAll();
    }

    public List<OrganizationAuditLog> getAllOrganizationAuditLogs() {
        return organizationAuditLogRepository.findAll();
    }

    /* ================= HELPER METHODS ================= */
    private Long getEntityId(Object entity) {
        if (entity instanceof User u) return u.getId();
        if (entity instanceof Passport p) return p.getId();
        if (entity instanceof Stamp s) return s.getId();
        return null;
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}

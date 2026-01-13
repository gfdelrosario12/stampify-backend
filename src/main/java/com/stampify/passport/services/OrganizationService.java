package com.stampify.passport.services;

import com.stampify.passport.models.*;
import com.stampify.passport.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final PassportRepository passportRepository;
    private final StampRepository stampRepository;
    private final OrganizationAuditLogRepository orgAuditLogRepository;

    @Autowired
    public OrganizationService(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            PassportRepository passportRepository,
            StampRepository stampRepository,
            OrganizationAuditLogRepository orgAuditLogRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.passportRepository = passportRepository;
        this.stampRepository = stampRepository;
        this.orgAuditLogRepository = orgAuditLogRepository;
    }

    public Organization createOrganization(Organization org, SuperAdmin actorUser, String actionCategory, String actionName) {
        Organization saved = organizationRepository.save(org);
        logAudit(actorUser, actionCategory, actionName, saved);
        return saved;
    }

    public Organization updateOrganization(Organization org, SuperAdmin actorUser, String actionCategory, String actionName) {
        Organization existing = organizationRepository.findById(org.getId())
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + org.getId()));

        existing.setName(org.getName());

        Organization saved = organizationRepository.save(existing);
        logAudit(actorUser, actionCategory, actionName, saved);
        return saved;
    }

    public void deleteOrganization(Long id, SuperAdmin actorUser, String actionCategory, String actionName) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        LocalDateTime now = LocalDateTime.now();

        // Soft delete all users (simplified)
        userRepository.findByOrganizationId(id).forEach(user -> user.softDelete(actorUser.getEmail()));

        org.markDeleted(now);
        organizationRepository.save(org);

        logAudit(actorUser, actionCategory, actionName, org);
    }

    private void logAudit(SuperAdmin actorUser, String actionCategory, String actionName, Organization entity) {
        OrganizationAuditLog auditLog = new OrganizationAuditLog();
        auditLog.setActorSuperAdminId(actorUser.getId());
        auditLog.setActionCategory(actionCategory);
        auditLog.setActionName(actionName);
        auditLog.setEntityName("Organization");
        auditLog.setOrganization(entity);

        orgAuditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public Optional<Organization> getById(Long id) {
        return organizationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Organization> getAll() {
        return organizationRepository.findAll();
    }

}

package com.stampify.passport.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final ScannerRepository scannerRepository;
    private final PassportRepository passportRepository;
    private final StampRepository stampRepository;
    private final AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository,
                               UserRepository userRepository,
                               MemberRepository memberRepository,
                               AdminRepository adminRepository,
                               ScannerRepository scannerRepository,
                               PassportRepository passportRepository,
                               StampRepository stampRepository,
                               AuditLogRepository auditLogRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.adminRepository = adminRepository;
        this.scannerRepository = scannerRepository;
        this.passportRepository = passportRepository;
        this.stampRepository = stampRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /* ============================
       CREATE
       ============================ */
    public Organization createOrganization(Organization org, User actorUser) throws Exception {
        org.setCreatedAt(LocalDateTime.now());
        Organization saved = organizationRepository.save(org);
        logAudit(actorUser, "ORGANIZATION", "CREATE", saved, null, saved);
        return saved;
    }

    /* ============================
       READ
       ============================ */
    public Optional<Organization> getById(Long id) {
        return organizationRepository.findById(id);
    }

    public List<Organization> getAll() {
        return organizationRepository.findAll();
    }

    /* ============================
       UPDATE
       ============================ */
    public Organization updateOrganization(Organization org, User actorUser) throws Exception {
        Organization existing = organizationRepository.findById(org.getId())
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + org.getId()));

        String previousData = serialize(existing);

        existing.setName(org.getName());
        existing.setUpdatedAt(LocalDateTime.now());

        Organization saved = organizationRepository.save(existing);
        logAudit(actorUser, "ORGANIZATION", "UPDATE", saved, previousData, saved);
        return saved;
    }

    /* ============================
       DELETE (SAFE / SOFT)
       ============================ */
    public void deleteOrganization(Long id, User actorUser) throws Exception {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + id));

        String previousData = serialize(org);
        LocalDateTime now = LocalDateTime.now();

        // Soft delete all users under this organization
        List<User> users = userRepository.findByOrganizationId(id);
        for (User user : users) {
            user.setActive(false);
            user.setUpdatedAt(now);

            if (user instanceof Member member) {
                // Soft delete member passports
                List<Passport> passports = passportRepository.findByMemberAndDeletedAtIsNull(member);
                for (Passport passport : passports) {
                    passport.setPassportStatus("REVOKED");
                    passport.setDeletedAt(now);

                    // Soft delete stamps
                    List<Stamp> stamps = stampRepository.findByPassportAndDeletedAtIsNull(passport);
                    for (Stamp stamp : stamps) {
                        stamp.setScanStatus("REVOKED");
                        stamp.setStampedAt(now);
                        stampRepository.save(stamp);
                    }

                    passportRepository.save(passport);
                }
            }

            if (user instanceof Scanner scanner) {
                // Soft delete stamps scanned by this scanner
                if (scanner.getStamps() != null) {
                    for (Stamp stamp : scanner.getStamps()) {
                        stamp.setScanStatus("REVOKED");
                        stamp.setStampedAt(now);
                        stampRepository.save(stamp);
                    }
                }
            }

            userRepository.save(user);
        }

        // Soft delete organization
        org.setDeletedAt(now);
        organizationRepository.save(org);

        logAudit(actorUser, "ORGANIZATION", "DELETE", org, previousData, null);
    }

    /* ============================
       HELPER METHODS
       ============================ */
    private void logAudit(User actorUser, String entityName, String actionName,
                          Object entity, String previousData, Object newData) throws Exception {

        AuditLog auditLog = new AuditLog();
        auditLog.setActorUser(actorUser);
        auditLog.setActionCategory(entityName);
        auditLog.setActionName(actionName);
        if (entity instanceof Organization o) auditLog.setEntityId(o.getId());
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

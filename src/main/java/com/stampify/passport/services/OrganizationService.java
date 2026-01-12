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
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final ScannerRepository scannerRepository;
    private final PassportRepository passportRepository;
    private final StampRepository stampRepository;
    private final OrganizationAuditLogRepository orgAuditLogRepository;

    @Autowired
    public OrganizationService(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            MemberRepository memberRepository,
            AdminRepository adminRepository,
            ScannerRepository scannerRepository,
            PassportRepository passportRepository,
            StampRepository stampRepository,
            OrganizationAuditLogRepository orgAuditLogRepository) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.adminRepository = adminRepository;
        this.scannerRepository = scannerRepository;
        this.passportRepository = passportRepository;
        this.stampRepository = stampRepository;
        this.orgAuditLogRepository = orgAuditLogRepository;
    }

    /* ============================
       CREATE
       ============================ */
    public Organization createOrganization(Organization org, SuperAdmin actorUser) {
        Organization saved = organizationRepository.save(org);
        logAudit(actorUser, "ORGANIZATION", "CREATE", saved);
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
    public Organization updateOrganization(Organization org, SuperAdmin actorUser) {

        Organization existing = organizationRepository.findById(org.getId())
                .orElseThrow(() ->
                        new RuntimeException("Organization not found with ID: " + org.getId()));

        existing.setName(org.getName());

        Organization saved = organizationRepository.save(existing);
        logAudit(actorUser, "ORGANIZATION", "UPDATE", saved);
        return saved;
    }

    /* ============================
       DELETE (SOFT)
       ============================ */
    public void deleteOrganization(Long id, SuperAdmin actorUser) {

        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        LocalDateTime now = LocalDateTime.now();

        List<User> users = userRepository.findByOrganizationId(id);

        for (User user : users) {

            user.softDelete(actorUser.getEmail());

            if (user instanceof Member member) {

                List<Passport> passports =
                        passportRepository.findByMemberAndDeletedAtIsNull(member);

                for (Passport passport : passports) {
                    passport.setPassportStatus("REVOKED");
                    passport.setDeletedAt(now);

                    List<Stamp> stamps =
                            stampRepository.findByPassportAndDeletedAtIsNull(passport);

                    for (Stamp stamp : stamps) {
                        stamp.setValid(false);
                        stamp.setDeletedAt(now);
                    }
                }
            }

            if (user instanceof OrgScanner scanner) {

                List<Stamp> stamps =
                        stampRepository.findByScannerAndDeletedAtIsNull(scanner);

                for (Stamp stamp : stamps) {
                    stamp.setValid(false);
                    stamp.setDeletedAt(now);
                }
            }
        }

        org.markDeleted(now);
        organizationRepository.save(org);

        logAudit(actorUser, "ORGANIZATION", "DELETE", org);
    }

    /* ============================
       HELPER METHODS
       ============================ */
    private void logAudit(
            SuperAdmin actorUser,
            String actionCategory,
            String actionName,
            Organization organization) {

        OrganizationAuditLog auditLog = new OrganizationAuditLog();
        auditLog.setActorSuperAdminId(actorUser.getId());
        auditLog.setActionCategory(actionCategory);
        auditLog.setActionName(actionName);
        auditLog.setEntityName("Organization");
        auditLog.setOrganization(organization);
        // occurredAt handled by @PrePersist

        orgAuditLogRepository.save(auditLog);
    }
}

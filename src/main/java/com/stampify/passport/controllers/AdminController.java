package com.stampify.passport.controllers;

import com.stampify.passport.dto.RegisterUserRequest;
import com.stampify.passport.models.*;
import com.stampify.passport.repositories.SuperAdminRepository;
import com.stampify.passport.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admins")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private OrganizationService organizationService;
    @Autowired private PassportService passportService;
    @Autowired private StampService stampService;
    @Autowired private AuditService auditService;
    @Autowired private SuperAdminRepository superAdminRepository;

    /* ==========================
       USER MANAGEMENT
       ========================== */

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody RegisterUserRequest req, @RequestParam Long actorId) throws Exception {
        User actorUser = userService.getById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Actor user not found"));
        User created = userService.createUser(req, actorUser);

        // Log creation
        Organization org = actorUser.getOrganization();
        auditService.logEntityAction(actorUser, org, "USER", "CREATE", created, null);

        return ResponseEntity.ok(created);
    }

    @GetMapping("/users")
    public ResponseEntity<List<AuthUserResponse>> getAllUsers(@RequestParam(required = false) Long organizationId) {
        List<User> all = userService.getAllUsers();

        if (organizationId != null) {
            all = all.stream()
                    .filter(u -> u.getOrganization() != null && organizationId.equals(u.getOrganization().getId()))
                    .collect(Collectors.toList());
        }

        List<AuthUserResponse> resp = all.stream()
                .map(AuthUserResponse::fromUser)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody User incoming,
                                           @RequestParam Long actorId) throws Exception {
        User actorUser = userService.getById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Actor user not found"));
        User previousData = userService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User updated = userService.editUser(id, incoming, actorUser)
                .orElseThrow(() -> new RuntimeException("Update failed"));

        // Log update
        Organization org = actorUser.getOrganization();
        auditService.logEntityAction(actorUser, org, "USER", "UPDATE", updated, previousData);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestParam Long actorId) throws Exception {
        User actorUser = userService.getById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Actor user not found"));
        User toDelete = userService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userService.deleteUser(id, actorUser);

        // Log deletion
        Organization org = actorUser.getOrganization();
        auditService.logEntityAction(actorUser, org, "USER", "DELETE", null, toDelete);

        return ResponseEntity.ok().build();
    }

    /* ==========================
       ORGANIZATION MANAGEMENT
       ========================== */

    @PostMapping("/organizations")
    public ResponseEntity<Organization> createOrganization(@RequestBody OrganizationRequest request) {
        SuperAdmin actor = superAdminRepository.findById(request.getSuperAdminId())
                .orElseThrow(() -> new RuntimeException("Super admin not found with ID: " + request.getSuperAdminId()));

        Organization org = new Organization();
        org.setName(request.getName());

        Organization created = organizationService.createOrganization(
                org, actor, "ORGANIZATION", "CREATE_ORGANIZATION"
        );

        return ResponseEntity.ok(created);
    }

    @PutMapping("/organizations/{id}")
    public ResponseEntity<Organization> updateOrganization(@PathVariable Long id,
                                                           @RequestBody OrganizationRequest request) {
        SuperAdmin actor = superAdminRepository.findById(request.getSuperAdminId())
                .orElseThrow(() -> new RuntimeException("Super admin not found with ID: " + request.getSuperAdminId()));

        Organization org = new Organization();
        org.setId(id);
        org.setName(request.getName());

        Organization updated = organizationService.updateOrganization(
                org, actor, "ORGANIZATION", "UPDATE_ORGANIZATION"
        );

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/organizations/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id,
                                                   @RequestParam Long superAdminId) {
        SuperAdmin actor = superAdminRepository.findById(superAdminId)
                .orElseThrow(() -> new RuntimeException("Super admin not found with ID: " + superAdminId));

        organizationService.deleteOrganization(id, actor, "ORGANIZATION", "DELETE_ORGANIZATION");

        return ResponseEntity.noContent().build();
    }

    /* ===== REQUEST DTO ===== */
    public static class OrganizationRequest {
        private String name;
        private Long superAdminId;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Long getSuperAdminId() { return superAdminId; }
        public void setSuperAdminId(Long superAdminId) { this.superAdminId = superAdminId; }
    }

    /* ==========================
       PASSPORT & STAMP MANAGEMENT
       ========================== */

    @GetMapping("/passports")
    public ResponseEntity<List<Passport>> getAllPassports() {
        return ResponseEntity.ok(passportService.getAllPassports());
    }

    @GetMapping("/passports/{id}")
    public ResponseEntity<Passport> getPassport(@PathVariable Long id) {
        return passportService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/passports/{id}/stamps")
    public ResponseEntity<List<Stamp>> getStampsByPassport(@PathVariable Long id) {
        return ResponseEntity.ok(stampService.getByPassportId(id));
    }

    /* ==========================
       AUDIT & LOGS VIEW
       ========================== */

    @GetMapping("/audit/logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditService.getAllAuditLogs());
    }

    @GetMapping("/audit/login-events")
    public ResponseEntity<List<AuditLoginEvent>> getAuditLoginEvents() {
        return ResponseEntity.ok(auditService.getAllAuditLoginEvents());
    }

    @GetMapping("/audit/password-changes")
    public ResponseEntity<List<AuditPasswordChange>> getAuditPasswordChanges() {
        return ResponseEntity.ok(auditService.getAllAuditPasswordChanges());
    }

    @GetMapping("/audit/stamp-actions")
    public ResponseEntity<List<AuditStampAction>> getAuditStampActions() {
        return ResponseEntity.ok(auditService.getAllAuditStampActions());
    }

    @GetMapping("/audit/organization")
    public ResponseEntity<List<OrganizationAuditLog>> getOrganizationAuditLogs() {
        return ResponseEntity.ok(auditService.getAllOrganizationAuditLogs());
    }

    /* ==========================
       Helper DTOs
       ========================== */

    public static class AuthUserResponse {
        public Long id;
        public String firstName;
        public String lastName;
        public String email;
        public String role;
        public Long organizationId;
        public Boolean isActive = true;
        public Object createdAt;
        public Object updatedAt;

        public static AuthUserResponse fromUser(User u) {
            AuthUserResponse r = new AuthUserResponse();
            r.id = u.getId();
            r.firstName = u.getFirstName();
            r.lastName = u.getLastName();
            r.email = u.getEmail();
            r.role = (u != null && u.getClass() != null) ? u.getClass().getSimpleName().toUpperCase() : null;
            r.organizationId = (u.getOrganization() != null) ? u.getOrganization().getId() : null;
            try { r.createdAt = u.getCreatedAt(); } catch (Exception ignored) {}
            try { r.updatedAt = u.getUpdatedAt(); } catch (Exception ignored) {}
            return r;
        }
    }

}

package com.stampify.passport.controllers;

import com.stampify.passport.models.Organization;
import com.stampify.passport.models.SuperAdmin;
import com.stampify.passport.repositories.SuperAdminRepository;
import com.stampify.passport.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final SuperAdminRepository superAdminRepository;

    @Autowired
    public OrganizationController(OrganizationService organizationService,
                                  SuperAdminRepository superAdminRepository) {
        this.organizationService = organizationService;
        this.superAdminRepository = superAdminRepository;
    }

    /* ===== CREATE ===== */
    @PostMapping
    public ResponseEntity<Organization> createOrganization(
            @RequestBody OrganizationRequest request) {

        SuperAdmin actorUser = superAdminRepository.findById(request.getSuperAdminId())
                .orElseThrow(() ->
                        new RuntimeException("Super admin not found with ID: " + request.getSuperAdminId()));

        Organization org = new Organization();
        org.setName(request.getName());

        Organization created = organizationService.createOrganization(
                org,
                actorUser,
                "ORGANIZATION",
                "CREATE_ORGANIZATION"
        );

        return ResponseEntity.ok(created);
    }

    /* ===== READ ===== */
    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganization(@PathVariable Long id) {
        return organizationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Organization>> getAllOrganizations() {
        return ResponseEntity.ok(organizationService.getAll());
    }

    /* ===== UPDATE ===== */
    @PutMapping("/{id}")
    public ResponseEntity<Organization> updateOrganization(
            @PathVariable Long id,
            @RequestBody OrganizationRequest request) {

        SuperAdmin actorUser = superAdminRepository.findById(request.getSuperAdminId())
                .orElseThrow(() ->
                        new RuntimeException("Super admin not found with ID: " + request.getSuperAdminId()));

        Organization org = new Organization();
        org.setId(id);
        org.setName(request.getName());

        Organization updated = organizationService.updateOrganization(
                org,
                actorUser,
                "ORGANIZATION",
                "UPDATE_ORGANIZATION"
        );

        return ResponseEntity.ok(updated);
    }

    /* ===== DELETE ===== */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(
            @PathVariable Long id,
            @RequestParam Long superAdminId) {

        SuperAdmin actorUser = superAdminRepository.findById(superAdminId)
                .orElseThrow(() ->
                        new RuntimeException("Super admin not found with ID: " + superAdminId));

        organizationService.deleteOrganization(
                id,
                actorUser,
                "ORGANIZATION",
                "DELETE_ORGANIZATION"
        );

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
}

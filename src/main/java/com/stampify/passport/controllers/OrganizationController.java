package com.stampify.passport.controllers;

import com.stampify.passport.models.Admin;
import com.stampify.passport.models.Organization;
import com.stampify.passport.services.OrganizationService;
import com.stampify.passport.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final AdminRepository adminRepository;

    @Autowired
    public OrganizationController(OrganizationService organizationService,
                                  AdminRepository adminRepository) {
        this.organizationService = organizationService;
        this.adminRepository = adminRepository;
    }

    /* ===== CREATE ===== */
    @PostMapping
    public ResponseEntity<Organization> createOrganization(@RequestBody Organization org) throws Exception {
        Admin actorUser = getCurrentAdmin();
        Organization created = organizationService.createOrganization(org, actorUser);
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
    public ResponseEntity<Organization> updateOrganization(@PathVariable Long id,
                                                           @RequestBody Organization org) throws Exception {
        Admin actorUser = getCurrentAdmin();
        org.setId(id);
        Organization updated = organizationService.updateOrganization(org, actorUser);
        return ResponseEntity.ok(updated);
    }

    /* ===== DELETE (SAFE / SOFT) ===== */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) throws Exception {
        Admin actorUser = getCurrentAdmin();
        organizationService.deleteOrganization(id, actorUser);
        return ResponseEntity.noContent().build();
    }

    /* ===== HELPER ===== */
    private Admin getCurrentAdmin() {
        // TODO: Replace this with actual JWT / SecurityContext authentication
        // For now, just fetching the first admin as placeholder
        Long adminId = 1L; // Replace with ID from JWT or frontend
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
    }
}

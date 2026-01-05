package com.stampify.passport.services;

import com.stampify.passport.models.Organization;
import com.stampify.passport.repositories.OrganizationRepository;
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

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /* ============================
       CREATE
       ============================ */
    public Organization createOrganization(Organization org) {
        return organizationRepository.save(org);
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
    public Organization updateOrganization(Organization org) {
        Organization existing = organizationRepository.findById(org.getId())
                .orElseThrow(() ->
                        new RuntimeException("Organization not found with ID: " + org.getId())
                );

        existing.setName(org.getName());
        // updatedAt handled by @PreUpdate

        return organizationRepository.save(existing);
    }

    /* ============================
       DELETE (ORPHAN SAFE)
       ============================ */
    public void deleteOrganization(Long id) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Organization not found with ID: " + id)
                );

        /*
         * CASCADE + ORPHAN REMOVAL WILL:
         * - delete admins
         * - delete members -> passports
         * - delete scanners -> stamps
         * - delete events
         * - delete joined users table rows
         */
        organizationRepository.delete(organization);
    }
}

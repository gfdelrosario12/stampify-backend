package com.stampify.passport.services;

import com.stampify.passport.models.Organization;
import com.stampify.passport.repositories.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    // ============================
    // CREATE ORGANIZATION
    // ============================
    public Organization createOrganization(Organization org) {
        org.setCreatedAt(LocalDateTime.now());
        org.setUpdatedAt(LocalDateTime.now());
        return organizationRepository.save(org);
    }

    // ============================
    // GET BY ID
    // ============================
    public Optional<Organization> getById(Long id) {
        return organizationRepository.findById(id);
    }

    // ============================
    // GET ALL
    // ============================
    public List<Organization> getAll() {
        return organizationRepository.findAll();
    }

    // ============================
    // UPDATE
    // ============================
    public Organization updateOrganization(Organization org) {
        Organization existing = organizationRepository.findById(org.getId())
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + org.getId()));

        existing.setName(org.getName());
        existing.setUpdatedAt(LocalDateTime.now());

        return organizationRepository.save(existing);
    }

    // ============================
    // DELETE
    // ============================
    public void deleteOrganization(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new RuntimeException("Organization not found with ID: " + id);
        }
        organizationRepository.deleteById(id);
    }
}

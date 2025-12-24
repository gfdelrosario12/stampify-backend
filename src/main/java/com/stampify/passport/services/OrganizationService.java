package com.stampify.passport.services;

import com.stampify.passport.models.Organization;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    public Organization createOrganization(Organization org) {
        return null;
    }

    public Optional<Organization> getById(Long id) {
        return Optional.empty();
    }

    public List<Organization> getAll() {
        return List.of();
    }

    public Organization updateOrganization(Organization org) {
        return null;
    }

    public void deleteOrganization(Long id) {
    }
}

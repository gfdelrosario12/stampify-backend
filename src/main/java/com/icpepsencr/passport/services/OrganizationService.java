package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Organization;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface OrganizationService {
    Organization createOrganization(Organization org);
    Optional<Organization> getById(Long id);
    List<Organization> getAll();
    Organization updateOrganization(Organization org);
    void deleteOrganization(Long id);
}

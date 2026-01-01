package com.stampify.passport.services;

import com.stampify.passport.models.Admin;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    public Optional<Admin> getById(Long id) {
        return Optional.empty();
    }

    public List<Admin> getByOrganization(Long orgId) {
        return List.of();
    }

    public void deleteAdmin(Long id) {
    }
}

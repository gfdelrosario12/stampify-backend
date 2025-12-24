package com.stampify.passport.services;

import com.stampify.passport.models.Admin;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    public Admin createAdmin(Admin admin) {
        return null;
    }

    public Optional<Admin> getById(Long id) {
        return Optional.empty();
    }

    public List<Admin> getByOrganization(Long orgId) {
        return List.of();
    }

    public Admin updateAdmin(Admin admin) {
        return null;
    }

    public void deleteAdmin(Long id) {
    }
}

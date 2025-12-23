package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Admin;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AdminService {
    Admin createAdmin(Admin admin);
    Optional<Admin> getById(Long id);
    List<Admin> getByOrganization(Long orgId);
    Admin updateAdmin(Admin admin);
    void deleteAdmin(Long id);
}

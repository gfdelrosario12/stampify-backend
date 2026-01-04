package com.stampify.passport.controllers;

import com.stampify.passport.dto.AdminDTO;
import com.stampify.passport.mappers.UserMapper;
import com.stampify.passport.models.Admin;
import com.stampify.passport.repositories.AdminRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminRepository adminRepository;

    public AdminController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /* ================= GET ALL ADMINS ================= */
    @GetMapping
    public List<AdminDTO> getAllAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(UserMapper::toAdminDTO)
                .toList();
    }

    /* ================= GET ADMIN BY ID ================= */
    @GetMapping("/{id}")
    public AdminDTO getAdminById(@PathVariable Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        return UserMapper.toAdminDTO(admin);
    }
}

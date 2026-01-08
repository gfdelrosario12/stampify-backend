package com.stampify.passport.controllers;

import com.stampify.passport.models.SuperAdmin;
import com.stampify.passport.repositories.SuperAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/super-admins")
public class SuperAdminController {

    private final SuperAdminRepository superAdminRepository;
    private final Argon2PasswordEncoder passwordEncoder;

    @Autowired
    public SuperAdminController(SuperAdminRepository superAdminRepository,
                                Argon2PasswordEncoder passwordEncoder) {
        this.superAdminRepository = superAdminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ===== CREATE ===== */
    @PostMapping
    public ResponseEntity<SuperAdmin> createSuperAdmin(@RequestBody SuperAdmin superAdmin) {
        if (superAdminRepository.existsByEmail(superAdmin.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Hash password using Argon2
        superAdmin.setPassword(passwordEncoder.encode(superAdmin.getPassword()));

        SuperAdmin saved = superAdminRepository.save(superAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /* ===== READ ALL ===== */
    @GetMapping
    public ResponseEntity<List<SuperAdmin>> getAllSuperAdmins() {
        return ResponseEntity.ok(superAdminRepository.findAll());
    }

    /* ===== READ ONE ===== */
    @GetMapping("/{id}")
    public ResponseEntity<SuperAdmin> getSuperAdmin(@PathVariable Long id) {
        SuperAdmin admin = superAdminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Super admin not found"));
        return ResponseEntity.ok(admin);
    }

    /* ===== UPDATE ===== */
    @PutMapping("/{id}")
    public ResponseEntity<SuperAdmin> updateSuperAdmin(@PathVariable Long id,
                                                       @RequestBody SuperAdmin adminDetails) {
        SuperAdmin superAdmin = superAdminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Super admin not found"));

        superAdmin.setFirstName(adminDetails.getFirstName());
        superAdmin.setLastName(adminDetails.getLastName());
        superAdmin.setEmail(adminDetails.getEmail());

        // Hash new password if provided
        if (adminDetails.getPassword() != null && !adminDetails.getPassword().isEmpty()) {
            superAdmin.setPassword(passwordEncoder.encode(adminDetails.getPassword()));
        }

        SuperAdmin updated = superAdminRepository.save(superAdmin);
        return ResponseEntity.ok(updated);
    }

    /* ===== DELETE ===== */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuperAdmin(@PathVariable Long id) {
        SuperAdmin superAdmin = superAdminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Super admin not found"));

        superAdminRepository.delete(superAdmin);
        return ResponseEntity.noContent().build();
    }
}

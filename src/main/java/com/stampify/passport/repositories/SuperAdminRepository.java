package com.stampify.passport.repositories;

import com.stampify.passport.models.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {

    Optional<SuperAdmin> findByEmail(String email);

    boolean existsByEmail(String email);
}

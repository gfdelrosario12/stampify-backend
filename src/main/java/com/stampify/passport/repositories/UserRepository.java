package com.stampify.passport.repositories;

import com.stampify.passport.models.User;
import com.stampify.passport.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Or, if you prefer using organization id directly
    List<User> findByOrganizationId(Long organizationId);
}

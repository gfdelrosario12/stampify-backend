package com.icpepsencr.passport.repositories;
import com.icpepsencr.passport.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    List<Admin> findByOrganizationId(Long orgId);
}
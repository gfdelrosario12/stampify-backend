package com.stampify.passport.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import com.stampify.passport.models.Scanner;

@Repository
public interface ScannerRepository extends JpaRepository<Scanner, Long> {
    List<Scanner> findByOrganizationId(Long orgId);
    Optional<Scanner> findByDeviceIdentifier(String deviceIdentifier);
}
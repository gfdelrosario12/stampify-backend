package com.stampify.passport.repositories;

import com.stampify.passport.models.Passport;
import com.stampify.passport.models.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StampRepository extends JpaRepository<Stamp, Long> {

    // Find a stamp by passport ID and event ID (unique)
    Optional<Stamp> findByPassport_IdAndEvent_Id(Long passportId, Long eventId);

    // Find all stamps for a passport that are not soft-deleted
    List<Stamp> findByPassport_IdAndScanStatusNot(Long passportId, String revokedStatus);

    // Find all stamps scanned by a specific scanner
    List<Stamp> findByScanner_Id(Long scannerId);

    // Fetch all stamps for a passport that are NOT soft-deleted
    List<Stamp> findByPassportAndDeletedAtIsNull(Passport passport);
}

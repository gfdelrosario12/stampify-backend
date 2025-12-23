package com.icpepsencr.passport.repositories;
import com.icpepsencr.passport.models.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StampRepository extends JpaRepository<Stamp, Long> {
    Optional<Stamp> findByPassportIdAndEventId(Long passportId, Long eventId);
    List<Stamp> findByScannerId(Long scannerId);
}
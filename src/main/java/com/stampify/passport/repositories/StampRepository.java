package com.stampify.passport.repositories;
import com.stampify.passport.models.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StampRepository extends JpaRepository<Stamp, Long> {
    Optional<Stamp> findByPassport_IdAndEvent_Id(Long passportId, Long eventId);

    List<Stamp> findByScanner_Id(Long scannerId);
}
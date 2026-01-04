package com.stampify.passport.services;

import com.stampify.passport.models.Stamp;
import com.stampify.passport.repositories.StampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StampService {

    @Autowired
    private StampRepository stampRepository;

    /**
     * Create a stamp
     * - Prevents duplicate stamp per passport + event
     * - Auto sets timestamps
     */
    public Stamp createStamp(Stamp stamp) {

        Optional<Stamp> existingStamp =
                stampRepository.findByPassport_IdAndEvent_Id(
                        stamp.getPassport().getId(),
                        stamp.getEvent().getId()
                );

        if (existingStamp.isPresent()) {
            throw new IllegalStateException(
                    "Passport already stamped for this event"
            );
        }

        stamp.setCreatedAt(LocalDateTime.now());
        stamp.setStampedAt(LocalDateTime.now());

        if (stamp.getScanStatus() == null) {
            stamp.setScanStatus("SUCCESS");
        }

        return stampRepository.save(stamp);
    }

    /**
     * Get stamp by ID
     */
    public Optional<Stamp> getById(Long id) {
        return stampRepository.findById(id);
    }

    /**
     * Get stamp by passport + event
     */
    public Optional<Stamp> getByPassportAndEvent(Long passportId, Long eventId) {
        return stampRepository.findByPassport_IdAndEvent_Id(passportId, eventId);
    }

    /**
     * Get all stamps scanned by a specific scanner
     */
    public List<Stamp> getByScannerId(Long scannerId) {
        return stampRepository.findByScanner_Id(scannerId);
    }

    /**
     * Update stamp
     * - Does NOT allow changing passport/event (important for integrity)
     */
    public Stamp updateStamp(Stamp updatedStamp) {

        Stamp existingStamp = stampRepository.findById(updatedStamp.getId())
                .orElseThrow(() -> new IllegalArgumentException("Stamp not found"));

        existingStamp.setScanStatus(updatedStamp.getScanStatus());
        existingStamp.setStampedAt(updatedStamp.getStampedAt());

        return stampRepository.save(existingStamp);
    }

    /**
     * Delete stamp
     */
    public void deleteStamp(Long id) {
        if (!stampRepository.existsById(id)) {
            throw new IllegalArgumentException("Stamp not found");
        }
        stampRepository.deleteById(id);
    }
}

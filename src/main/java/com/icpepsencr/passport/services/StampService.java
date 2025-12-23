package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Stamp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface StampService {
    Stamp createStamp(Stamp stamp);
    Optional<Stamp> getById(Long id);
    Optional<Stamp> getByPassportAndEvent(Long passportId, Long eventId);
    List<Stamp> getByScannerId(Long scannerId);
    Stamp updateStamp(Stamp stamp);
    void deleteStamp(Long id);
}

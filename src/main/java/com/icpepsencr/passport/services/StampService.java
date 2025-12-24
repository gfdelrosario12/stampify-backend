package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Stamp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StampService {

    public Stamp createStamp(Stamp stamp) {
        return null;
    }

    public Optional<Stamp> getById(Long id) {
        return Optional.empty();
    }

    public Optional<Stamp> getByPassportAndEvent(Long passportId, Long eventId) {
        return Optional.empty();
    }

    public List<Stamp> getByScannerId(Long scannerId) {
        return List.of();
    }

    public Stamp updateStamp(Stamp stamp) {
        return null;
    }

    public void deleteStamp(Long id) {
    }
}

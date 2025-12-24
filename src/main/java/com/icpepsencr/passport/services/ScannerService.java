package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Scanner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScannerService {

    public Scanner createScanner(Scanner scanner) {
        return null;
    }

    public Optional<Scanner> getById(Long id) {
        return Optional.empty();
    }

    public List<Scanner> getByOrganization(Long orgId) {
        return List.of();
    }

    public Optional<Scanner> getByDeviceIdentifier(String deviceIdentifier) {
        return Optional.empty();
    }

    public Scanner updateScanner(Scanner scanner) {
        return null;
    }

    public void deleteScanner(Long id) {
    }
}

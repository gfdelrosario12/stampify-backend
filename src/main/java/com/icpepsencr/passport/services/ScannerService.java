package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Scanner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ScannerService {
    Scanner createScanner(Scanner scanner);
    Optional<Scanner> getById(Long id);
    List<Scanner> getByOrganization(Long orgId);
    Optional<Scanner> getByDeviceIdentifier(String deviceIdentifier);
    Scanner updateScanner(Scanner scanner);
    void deleteScanner(Long id);
}

package com.stampify.passport.services;

import com.stampify.passport.models.Scanner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScannerService {

    public Optional<Scanner> getById(Long id) {
        return Optional.empty();
    }

    public List<Scanner> getByOrganization(Long orgId) {
        return List.of();
    }

    public Optional<Scanner> getByDeviceIdentifier(String deviceIdentifier) {
        return Optional.empty();
    }

    public void deleteScanner(Long id) {
    }
}

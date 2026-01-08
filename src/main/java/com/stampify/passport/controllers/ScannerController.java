package com.stampify.passport.controllers;

import com.stampify.passport.dto.ScannerDTO;
import com.stampify.passport.mappers.UserMapper;
import com.stampify.passport.models.OrgScanner;
import com.stampify.passport.repositories.ScannerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scanners")
public class ScannerController {

    private final ScannerRepository scannerRepository;

    public ScannerController(ScannerRepository scannerRepository) {
        this.scannerRepository = scannerRepository;
    }

    /* ================= GET ALL SCANNERS ================= */
    @GetMapping
    public List<ScannerDTO> getAllScanners() {
        return scannerRepository.findAll()
                .stream()
                .map(UserMapper::toScannerDTO)
                .toList();
    }

    /* ================= GET SCANNER BY ID ================= */
    @GetMapping("/{id}")
    public ScannerDTO getScannerById(@PathVariable Long id) {
        OrgScanner scanner = scannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scanner not found"));
        return UserMapper.toScannerDTO(scanner);
    }
}

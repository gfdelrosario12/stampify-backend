package com.icpepsencr.passport.controllers;

import com.icpepsencr.passport.models.Scanner;
import com.icpepsencr.passport.services.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scanners")
public class ScannerController {

    @Autowired
    private ScannerService scannerService;

    @PostMapping
    public ResponseEntity<Scanner> createScanner(@RequestBody Scanner scanner) {
        return ResponseEntity.ok(scannerService.createScanner(scanner));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scanner> getScanner(@PathVariable Long id) {
        return scannerService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<Scanner>> getScannersByOrganization(@PathVariable Long orgId) {
        return ResponseEntity.ok(scannerService.getByOrganization(orgId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Scanner> updateScanner(@PathVariable Long id, @RequestBody Scanner scanner) {
        scanner.setId(id);
        return ResponseEntity.ok(scannerService.updateScanner(scanner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScanner(@PathVariable Long id) {
        scannerService.deleteScanner(id);
        return ResponseEntity.noContent().build();
    }
}

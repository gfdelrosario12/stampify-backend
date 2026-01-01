package com.stampify.passport.controllers;

import com.stampify.passport.models.Scanner;
import com.stampify.passport.services.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scanners")
public class ScannerController {

    @Autowired
    private ScannerService scannerService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScanner(@PathVariable Long id) {
        scannerService.deleteScanner(id);
        return ResponseEntity.noContent().build();
    }
}

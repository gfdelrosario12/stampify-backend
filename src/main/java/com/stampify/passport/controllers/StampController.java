package com.stampify.passport.controllers;

import com.stampify.passport.models.Stamp;
import com.stampify.passport.services.StampService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stamps")
public class StampController {

    @Autowired
    private StampService stampService;

    @PostMapping
    public ResponseEntity<Stamp> createStamp(@RequestBody Stamp stamp) {
        return ResponseEntity.ok(stampService.createStamp(stamp));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stamp> getStamp(@PathVariable Long id) {
        return stampService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/passport/{passportId}/event/{eventId}")
    public ResponseEntity<Stamp> getStampByPassportAndEvent(@PathVariable Long passportId,
                                                            @PathVariable Long eventId) {
        return stampService.getByPassportAndEvent(passportId, eventId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/scanner/{scannerId}")
    public ResponseEntity<List<Stamp>> getStampsByScanner(@PathVariable Long scannerId) {
        return ResponseEntity.ok(stampService.getByScannerId(scannerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stamp> updateStamp(@PathVariable Long id, @RequestBody Stamp stamp) {
        stamp.setId(id);
        return ResponseEntity.ok(stampService.updateStamp(stamp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStamp(@PathVariable Long id) {
        stampService.deleteStamp(id);
        return ResponseEntity.noContent().build();
    }
}

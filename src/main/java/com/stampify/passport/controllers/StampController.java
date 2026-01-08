package com.stampify.passport.controllers;

import com.stampify.passport.models.Stamp;
import com.stampify.passport.models.User;
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

    /**
     * Placeholder method for getting the logged-in user
     * Replace this with your real auth logic
     */
    private User getCurrentUser() {
        // TODO: get from JWT/session
        return null;
    }

    @PostMapping
    public ResponseEntity<Stamp> createStamp(@RequestBody Stamp stamp) {
        try {
            User actorUser = getCurrentUser();
            return ResponseEntity.ok(stampService.createStamp(stamp, actorUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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

    @GetMapping("/passport/{passportId}")
    public ResponseEntity<List<Stamp>> getStampsByPassport(@PathVariable Long passportId) {
        return ResponseEntity.ok(stampService.getByPassportId(passportId));
    }

    @GetMapping("/scanner/{scannerId}")
    public ResponseEntity<List<Stamp>> getStampsByScanner(@PathVariable Long scannerId) {
        return ResponseEntity.ok(stampService.getByScannerId(scannerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stamp> updateStamp(@PathVariable Long id, @RequestBody Stamp stamp) {
        try {
            stamp.setId(id);
            User actorUser = getCurrentUser();
            return ResponseEntity.ok(stampService.updateStamp(stamp, actorUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStamp(@PathVariable Long id) {
        try {
            User actorUser = getCurrentUser();
            stampService.deleteStamp(id, actorUser);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

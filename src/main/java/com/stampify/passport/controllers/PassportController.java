package com.stampify.passport.controllers;

import com.stampify.passport.models.Passport;
import com.stampify.passport.services.PassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passports")
public class PassportController {

    @Autowired
    private PassportService passportService;

    @GetMapping("/{id}")
    public ResponseEntity<Passport> getPassport(@PathVariable Long id) {
        return passportService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Passport>> getPassportsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(passportService.getByMemberId(memberId));
    }
}

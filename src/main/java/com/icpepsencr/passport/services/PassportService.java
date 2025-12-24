package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Passport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PassportService {

    public Passport createPassport(Passport passport) {
        return null;
    }

    public Optional<Passport> getById(Long id) {
        return Optional.empty();
    }

    public List<Passport> getByMemberId(Long memberId) {
        return List.of();
    }

    public Passport updatePassport(Passport passport) {
        return null;
    }

    public void deletePassport(Long id) {
    }
}

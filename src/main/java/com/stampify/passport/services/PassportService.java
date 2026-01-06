package com.stampify.passport.services;

import com.stampify.passport.models.Passport;
import com.stampify.passport.repositories.PassportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PassportService {

    private final PassportRepository passportRepository;

    public PassportService(PassportRepository passportRepository) {
        this.passportRepository = passportRepository;
    }

    public Optional<Passport> getById(Long id) {
        return passportRepository.findById(id);
    }

    public List<Passport> getByMemberId(Long memberId) {
        return passportRepository.findByMemberId(memberId);
    }
}

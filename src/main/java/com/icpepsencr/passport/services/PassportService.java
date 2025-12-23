package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Passport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PassportService {
    Passport createPassport(Passport passport);
    Optional<Passport> getById(Long id);
    List<Passport> getByMemberId(Long memberId);
    Passport updatePassport(Passport passport);
    void deletePassport(Long id);
}

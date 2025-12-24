package com.stampify.passport.repositories;
import com.stampify.passport.models.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {
    List<Passport> findByMemberId(Long memberId);
}
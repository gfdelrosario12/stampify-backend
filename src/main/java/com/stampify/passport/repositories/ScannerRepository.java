package com.stampify.passport.repositories;
import com.stampify.passport.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import com.stampify.passport.models.Scanner;

@Repository
public interface ScannerRepository extends JpaRepository<Scanner, Long> {
    Optional<Scanner> findById(Long id);
}
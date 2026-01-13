    package com.stampify.passport.repositories;
    import com.stampify.passport.models.AuditStampAction;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface AuditStampActionRepository extends JpaRepository<AuditStampAction, Long> { }
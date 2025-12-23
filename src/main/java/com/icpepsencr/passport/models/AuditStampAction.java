package com.icpepsencr.passport.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_stamp_actions")
public class AuditStampAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "audit_log_id", nullable = false)
    private AuditLog auditLog;

    @ManyToOne
    @JoinColumn(name = "stamp_id")
    private Stamp stamp;

    private String action;

    @ManyToOne
    @JoinColumn(name = "performed_by_scanner_id")
    private Scanner performedByScanner;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }

    public void setAuditLog(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    public Stamp getStamp() {
        return stamp;
    }

    public void setStamp(Stamp stamp) {
        this.stamp = stamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Scanner getPerformedByScanner() {
        return performedByScanner;
    }

    public void setPerformedByScanner(Scanner performedByScanner) {
        this.performedByScanner = performedByScanner;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
}

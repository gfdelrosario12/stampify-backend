package com.stampify.passport.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stamps", uniqueConstraints = @UniqueConstraint(columnNames = {"passport_id", "event_id"}))
public class Stamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "passport_id", nullable = false)
    private Passport passport;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "scanner_id", nullable = false)
    private Scanner scanner;

    private LocalDateTime stampedAt;
    private String scanStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For safe delete / orphan handling
    private Boolean valid = true;
    private LocalDateTime deletedAt;

    /* ===== GETTERS / SETTERS ===== */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Passport getPassport() { return passport; }
    public void setPassport(Passport passport) { this.passport = passport; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public Scanner getScanner() { return scanner; }
    public void setScanner(Scanner scanner) { this.scanner = scanner; }

    public LocalDateTime getStampedAt() { return stampedAt; }
    public void setStampedAt(LocalDateTime stampedAt) { this.stampedAt = stampedAt; }

    public String getScanStatus() { return scanStatus; }
    public void setScanStatus(String scanStatus) { this.scanStatus = scanStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getValid() { return valid; }
    public void setValid(Boolean valid) { this.valid = valid; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

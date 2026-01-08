package com.stampify.passport.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organization {

    /* ================= PRIMARY KEY ================= */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= BASIC INFO ================= */

    @Column(nullable = false, unique = true)
    private String name;

    /* ================= AUDIT FIELDS ================= */

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /* ================= RELATIONSHIPS ================= */

    /**
     * IMPORTANT:
     * - No cascade
     * - No orphanRemoval
     * - Organization MUST be creatable without users
     * - Soft delete propagation handled in service layer
     */

    @OneToMany(mappedBy = "organization")
    private List<Admin> admins = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<OrgScanner> scanners = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<Event> events = new ArrayList<>();

    /* ================= LIFECYCLE ================= */

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ================= SOFT DELETE ================= */

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void markDeleted(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public List<Member> getMembers() {
        return members;
    }

    public List<OrgScanner> getScanners() {
        return scanners;
    }

    public List<Event> getEvents() {
        return events;
    }

    /* ================= RESTRICTED SETTERS ================= */

    /**
     * Intentionally limited setters to prevent
     * accidental state corruption.
     */

    public void setId(Long id) {
        this.id = id;
    }

    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    protected void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    protected void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}

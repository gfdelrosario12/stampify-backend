package com.stampify.passport.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organization_audit_logs")
public class OrganizationAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= ACTOR ================= */

    @Column(name = "actor_super_admin_id", nullable = false)
    private Long actorSuperAdminId;

    /* ================= ACTION ================= */

    @Column(nullable = false)
    private String actionCategory; // ORGANIZATION

    @Column(nullable = false)
    private String actionName; // CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private String entityName; // Organization

    /* ================= RELATIONSHIP ================= */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    /* ================= TIMESTAMP ================= */

    @Column(nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    @PrePersist
    protected void onCreate() {
        this.occurredAt = LocalDateTime.now();
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() { return id; }

    public Long getActorSuperAdminId() { return actorSuperAdminId; }
    public void setActorSuperAdminId(Long actorSuperAdminId) {
        this.actorSuperAdminId = actorSuperAdminId;
    }

    public String getActionCategory() { return actionCategory; }
    public void setActionCategory(String actionCategory) {
        this.actionCategory = actionCategory;
    }

    public String getActionName() { return actionName; }
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public LocalDateTime getOccurredAt() { return occurredAt; }
}

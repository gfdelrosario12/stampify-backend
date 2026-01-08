package com.stampify.passport.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organization_audit_logs")
public class OrganizationAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_super_admin_id", nullable = false)
    private Long actorSuperAdminId;

    @Column(nullable = false)
    private String actionCategory; // e.g., ORGANIZATION

    @Column(nullable = false)
    private String actionName; // e.g., CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private String entityName; // e.g., Organization

    private Long entityId;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    /* ===== Getters & Setters ===== */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActorSuperAdminId() { return actorSuperAdminId; }
    public void setActorSuperAdminId(Long actorSuperAdminId) { this.actorSuperAdminId = actorSuperAdminId; }

    public String getActionCategory() { return actionCategory; }
    public void setActionCategory(String actionCategory) { this.actionCategory = actionCategory; }

    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}

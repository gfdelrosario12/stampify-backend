package com.stampify.passport.dto;

import java.time.LocalDateTime;

public class AdminDTO extends UserDTO {

    /* ================= RELATION (ID ONLY) ================= */
    private Long organizationId;

    /* ================= ADMIN ================= */
    private LocalDateTime createdAt;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

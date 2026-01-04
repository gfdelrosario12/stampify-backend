package com.stampify.passport.dto;

import java.time.LocalDateTime;

public class MemberDTO extends UserDTO {

    /* ================= RELATION (ID ONLY) ================= */
    private Long organizationId;

    private Integer passportCount;

    /* ================= MEMBER ================= */
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private LocalDateTime createdAt;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getPassportCount() {
        return passportCount;
    }

    public void setPassportCount(Integer passportCount) {
        this.passportCount = passportCount;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

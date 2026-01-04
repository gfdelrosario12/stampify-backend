package com.stampify.passport.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "members")
@DiscriminatorValue("MEMBER")
public class Member extends User {

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member")
    private List<Passport> passports;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
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

    public List<Passport> getPassports() {
        return passports;
    }

    public void setPassports(List<Passport> passports) {
        this.passports = passports;
    }
}

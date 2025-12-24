package com.stampify.passport.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "scanners")
public class Scanner extends User {

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(unique = true)
    private String deviceIdentifier;

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime registeredAt;

    @OneToMany(mappedBy = "scanner")
    private List<Stamp> stamps;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public List<Stamp> getStamps() {
        return stamps;
    }

    public void setStamps(List<Stamp> stamps) {
        this.stamps = stamps;
    }
}

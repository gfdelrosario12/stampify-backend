package com.stampify.passport.dto;

import java.time.LocalDateTime;

public class ScannerDTO extends UserDTO {

    /* ================= RELATION (ID ONLY) ================= */
    private Integer stampCount;

    /* ================= SCANNER ================= */
    private String deviceIdentifier;
    private Boolean isActive;
    private LocalDateTime registeredAt;

    public Integer getStampCount() {
        return stampCount;
    }

    public void setStampCount(Integer stampCount) {
        this.stampCount = stampCount;
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
}

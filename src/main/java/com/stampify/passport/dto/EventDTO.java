package com.stampify.passport.dto;

import java.time.LocalDateTime;

public class EventDTO {
    private Long id;
    private String eventName;
    private String eventDescription;
    private String eventType;
    private String eventBadge;
    private String venueName;
    private String venueImageUrl;
    private LocalDateTime scheduledAt;

    private OrganizationDTO organization;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEventBadge() { return eventBadge; }
    public void setEventBadge(String eventBadge) { this.eventBadge = eventBadge; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public String getVenueImageUrl() { return venueImageUrl; }
    public void setVenueImageUrl(String venueImageUrl) { this.venueImageUrl = venueImageUrl; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public OrganizationDTO getOrganization() { return organization; }
    public void setOrganization(OrganizationDTO organization) { this.organization = organization; }
}

package com.stampify.passport.mappers;

import com.stampify.passport.dto.EventDTO;
import com.stampify.passport.dto.OrganizationDTO;
import com.stampify.passport.models.Event;
import com.stampify.passport.models.Organization;
import org.springframework.stereotype.Service;

@Service
public class EventMapper {

    private final OrganizationMapper organizationMapper;

    public EventMapper(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    public EventDTO toDTO(Event event) {
        if (event == null) return null;

        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setOrganization(organizationMapper.toDTO(event.getOrganization()));
        dto.setEventName(event.getEventName());
        dto.setEventDescription(event.getEventDescription());
        dto.setEventType(event.getEventType());
        dto.setEventBadge(event.getEventBadge());
        dto.setVenueName(event.getVenueName());
        dto.setVenueImageUrl(event.getVenueImageUrl());
        dto.setScheduledAt(event.getScheduledAt());

        return dto;
    }
}

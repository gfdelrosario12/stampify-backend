package com.stampify.passport.services;

import com.stampify.passport.models.Event;
import com.stampify.passport.repositories.EventRepository;
import com.stampify.passport.repositories.OrganizationRepository;
import com.stampify.passport.models.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * CREATE EVENT
     */
    public Event createEvent(Event event) {

        if (event.getOrganization() == null || event.getOrganization().getId() == null) {
            throw new RuntimeException("Organization is required");
        }

        Organization organization = organizationRepository
                .findById(event.getOrganization().getId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        event.setOrganization(organization);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    /**
     * GET EVENT BY ID
     */
    public Optional<Event> getById(Long id) {
        return eventRepository.findById(id);
    }

    /**
     * GET ALL EVENTS BY ORGANIZATION
     */
    public List<Event> getByOrganization(Long orgId) {
        return eventRepository.findByOrganizationId(orgId);
    }

    /**
     * UPDATE EVENT
     */
    public Event updateEvent(Event updatedEvent) {

        Event existingEvent = eventRepository.findById(updatedEvent.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        existingEvent.setEventName(updatedEvent.getEventName());
        existingEvent.setEventDescription(updatedEvent.getEventDescription());
        existingEvent.setEventType(updatedEvent.getEventType());
        existingEvent.setEventBadge(updatedEvent.getEventBadge());
        existingEvent.setVenueName(updatedEvent.getVenueName());
        existingEvent.setVenueImageUrl(updatedEvent.getVenueImageUrl());
        existingEvent.setScheduledAt(updatedEvent.getScheduledAt());
        existingEvent.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(existingEvent);
    }

    /**
     * DELETE EVENT
     */
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found");
        }
        eventRepository.deleteById(id);
    }
}

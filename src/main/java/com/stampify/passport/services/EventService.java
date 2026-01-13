package com.stampify.passport.services;

import com.stampify.passport.dto.EventDTO;
import com.stampify.passport.dto.OrganizationDTO;
import com.stampify.passport.mappers.OrganizationMapper;
import com.stampify.passport.models.Event;
import com.stampify.passport.models.Organization;
import com.stampify.passport.repositories.EventRepository;
import com.stampify.passport.repositories.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private S3FileStorageService s3FileStorageService;

    @Autowired
    private OrganizationMapper organizationMapper;

    /**
     * Convert Event entity to DTO
     */
    public EventDTO toDTO(Event event) {
        if (event == null) return null;

        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setEventName(event.getEventName());
        dto.setEventDescription(event.getEventDescription());
        dto.setEventType(event.getEventType());
        dto.setEventBadge(event.getEventBadge());
        dto.setVenueName(event.getVenueName());
        dto.setVenueImageUrl(event.getVenueImageUrl());
        dto.setScheduledAt(event.getScheduledAt());

        // Map organization as DTO
        if (event.getOrganization() != null) {
            OrganizationDTO orgDTO = organizationMapper.toDTO(event.getOrganization());
            dto.setOrganization(orgDTO);
        }

        return dto;
    }

    /**
     * Convert DTO → Event entity
     */
    public Event fromDTO(EventDTO dto) {
        if (dto == null) return null;

        Event event = new Event();
        event.setId(dto.getId());
        event.setEventName(dto.getEventName());
        event.setEventDescription(dto.getEventDescription());
        event.setEventType(dto.getEventType());
        event.setEventBadge(dto.getEventBadge());
        event.setVenueName(dto.getVenueName());
        event.setVenueImageUrl(dto.getVenueImageUrl());
        event.setScheduledAt(dto.getScheduledAt());

        if (dto.getOrganization() != null && dto.getOrganization().getId() != null) {
            Organization org = organizationRepository.findById(dto.getOrganization().getId())
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
            event.setOrganization(org);
        }

        return event;
    }

    /**
     * CREATE EVENT
     */
    @Transactional
    public EventDTO createEvent(EventDTO dto) {
        Event event = fromDTO(dto);

        // Ensure this is a NEW event
        event.setId(null);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        return toDTO(saved);
    }

    /**
     * GET EVENT BY ID
     */
    public Optional<EventDTO> getById(Long id) {
        return eventRepository.findById(id)
                .map(this::toDTO);
    }

    /**
     * GET EVENTS BY ORGANIZATION
     */
    public List<EventDTO> getByOrganization(Long orgId) {
        return eventRepository.findByOrganizationId(orgId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET ALL EVENTS
     */
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * UPDATE EVENT
     */
    @Transactional
    public EventDTO updateEvent(Long id, EventDTO dto) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Event updatedEvent = fromDTO(dto);

        // Update fields
        existingEvent.setEventName(updatedEvent.getEventName());
        existingEvent.setEventDescription(updatedEvent.getEventDescription());
        existingEvent.setEventType(updatedEvent.getEventType());
        existingEvent.setEventBadge(updatedEvent.getEventBadge());
        existingEvent.setVenueName(updatedEvent.getVenueName());
        existingEvent.setVenueImageUrl(updatedEvent.getVenueImageUrl());
        existingEvent.setScheduledAt(updatedEvent.getScheduledAt());
        existingEvent.setUpdatedAt(LocalDateTime.now());

        Event saved = eventRepository.save(existingEvent);
        return toDTO(saved);
    }

    /**
     * DELETE EVENT
     */
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Delete files from S3
        s3FileStorageService.deleteVenueImage(event.getVenueImageUrl());
        s3FileStorageService.deleteEventBadge(event.getEventBadge());

        eventRepository.delete(event);
    }
}

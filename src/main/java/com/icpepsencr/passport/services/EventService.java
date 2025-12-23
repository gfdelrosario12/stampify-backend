package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EventService {
    Event createEvent(Event event);
    Optional<Event> getById(Long id);
    List<Event> getByOrganization(Long orgId);
    Event updateEvent(Event event);
    void deleteEvent(Long id);
}

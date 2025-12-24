package com.stampify.passport.services;

import com.stampify.passport.models.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    public Event createEvent(Event event) {
        return null;
    }

    public Optional<Event> getById(Long id) {
        return Optional.empty();
    }

    public List<Event> getByOrganization(Long orgId) {
        return List.of();
    }

    public Event updateEvent(Event event) {
        return null;
    }

    public void deleteEvent(Long id) {
    }
}

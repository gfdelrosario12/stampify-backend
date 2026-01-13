package com.stampify.passport.controllers;

import com.stampify.passport.dto.EventDTO;
import com.stampify.passport.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    /** CREATE EVENT */
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        EventDTO created = eventService.createEvent(eventDTO);
        return ResponseEntity.ok(created);
    }

    /** GET EVENT BY ID */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Long id) {
        return eventService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET EVENTS BY ORGANIZATION */
    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<EventDTO>> getEventsByOrganization(@PathVariable Long orgId) {
        return ResponseEntity.ok(eventService.getByOrganization(orgId));
    }

    /** UPDATE EVENT */
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        EventDTO updated = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updated);
    }

    /** DELETE EVENT */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}

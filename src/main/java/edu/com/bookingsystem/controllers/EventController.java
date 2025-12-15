package edu.com.bookingsystem.controllers;


import edu.com.bookingsystem.api.EventApi;
import edu.com.bookingsystem.dtos.EventRequestDTO;
import edu.com.bookingsystem.dtos.EventResponseDTO;
import edu.com.bookingsystem.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EventController implements EventApi {

    private final EventService eventService;

    @Override
    public ResponseEntity<List<EventResponseDTO>> getList() {
        List<EventResponseDTO> response = eventService.getList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Integer> getNumberOfAvailableSpotsFPerEvent(UUID eventId) {
        Integer number = eventService.getAvailableSeats(eventId);
        return ResponseEntity.ok(number);
    }

    @Override
    public ResponseEntity<EventResponseDTO> createEvent(EventRequestDTO dto, Principal principal) {
        EventResponseDTO response = eventService.createEvent(dto, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<EventResponseDTO> updateEvent(UUID eventId, EventRequestDTO dto, Principal principal) {
        EventResponseDTO response = eventService.updateEvent(eventId, dto, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Boolean> deleteEvent(UUID eventId, Principal principal) {
        boolean response = eventService.deleteEvent(eventId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<EventResponseDTO>> getAllUpcomingByLocation(String location) {
        List<EventResponseDTO> response = eventService.getUpcomingByLocationList(location);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<EventResponseDTO>> getAllPastByLocation(String location) {
        List<EventResponseDTO> response = eventService.getPastByLocationList(location);
        return ResponseEntity.ok(response);
    }
}

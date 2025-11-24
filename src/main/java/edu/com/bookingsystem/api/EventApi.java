package edu.com.bookingsystem.api;


import edu.com.bookingsystem.dtos.EventRequestDTO;
import edu.com.bookingsystem.dtos.EventResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequestMapping(AuthApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with event", description = AuthApi.API_PATH_DICTIONARY)
public interface EventApi {

    String API_PATH_DICTIONARY = "/api/event";

    @GetMapping("/")
    @Operation(summary = "get all events. Available for all roles:")
    public ResponseEntity<List<EventResponseDTO>> getList();

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN' || 'SUPER_ADMIN')")
    @Operation(summary = "Create new event. Only available for roles: admin, super admin")
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventRequestDTO dto, Principal principal);

    // need principal for the field  createdBy/updatedBy

    @PutMapping("/update/{eventId}")
    @PreAuthorize("hasRole('ADMIN' || 'SUPER_ADMIN')")
    @Operation(summary = "Update existing event. Only available for roles: admin, super admin")
    public ResponseEntity<EventResponseDTO> updateEvent(@RequestParam UUID eventId, @RequestBody EventRequestDTO dto, Principal principal);

    @DeleteMapping("/delete/{eventId}")
    @PreAuthorize("hasRole('ADMIN' || 'SUPER_ADMIN')")
    @Operation(summary = "Delete existing event. Only available for roles: admin, super admin")
    public ResponseEntity<Boolean> deleteEvent(@RequestParam UUID eventId, Principal principal);
}

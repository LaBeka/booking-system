package edu.com.bookingsystem.api;


import edu.com.bookingsystem.dtos.EventRequestDTO;
import edu.com.bookingsystem.dtos.EventResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequestMapping(EventApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with EVENT", description = EventApi.API_PATH_DICTIONARY)
@Validated
public interface EventApi {

    String API_PATH_DICTIONARY = "/api/event";

    @GetMapping("/all")
    @Operation(summary = "get all events. NO ROLES REQUIRED")
    ResponseEntity<List<EventResponseDTO>> getList();

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create new event. Only available for roles: admin & super admin")
    ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO dto, Principal principal);

    @PutMapping("/update/{eventId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update existing event. Only available for roles: admin, super admin")
    ResponseEntity<EventResponseDTO> updateEvent(@PathVariable @NotNull(message = "event id is mandatory") UUID eventId, @Valid @RequestBody EventRequestDTO dto, Principal principal);

    @DeleteMapping("/delete/{eventId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete existing event. Only available for roles: admin, super admin")
    ResponseEntity<Boolean> deleteEvent(@PathVariable @NotNull(message = "event id is mandatory") UUID eventId, Principal principal);


    @GetMapping("/upcoming")
    @Operation(summary = "get all events. NO ROLES REQUIRED")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<List<EventResponseDTO>> getAllUpcomingByLocation(
            @RequestParam
            @NotBlank(message = "Location is mandatory")
            @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
            @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Location can only contain letters, spaces, or hyphens")
            String location);

    @GetMapping("/past")
    @Operation(summary = "get all events. NO ROLES REQUIRED")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<List<EventResponseDTO>> getAllPastByLocation(
            @RequestParam
            @NotBlank(message = "Location is mandatory")
            @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
            @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Location can only contain letters, spaces, or hyphens")
            String location);
}

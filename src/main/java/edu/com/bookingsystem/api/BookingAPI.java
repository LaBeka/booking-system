package edu.com.bookingsystem.api;


import edu.com.bookingsystem.dtos.BookingResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequestMapping(BookingAPI.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with booking", description = BookingAPI.API_PATH_DICTIONARY)
@Validated
public interface BookingAPI {

    String API_PATH_DICTIONARY = "/api/book";

    @GetMapping("/")
    @Operation(summary = "Get own bookings. Available for all roles:")
    ResponseEntity<List<BookingResponseDTO>> getOwnBookingList(Principal user);

    @GetMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get all bookings by event id. Available for admins")
    ResponseEntity<List<BookingResponseDTO>> getAllBookingsByEventId(@PathVariable @NotNull(message = "Event id is mandatory") UUID eventId);

    @PostMapping("/{eventId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Book the event. Available for users only")
    ResponseEntity<BookingResponseDTO> bookEvent(@PathVariable @NotNull(message = "Event id is mandatory") UUID eventId, Principal principal);

    @PutMapping("/unbook/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Book the event. Available for users")
    ResponseEntity<BookingResponseDTO> unBookEvent(@PathVariable @NotNull(message = "Booking id is mandatory") UUID bookingId, Principal principal);

    @DeleteMapping("/delete/{bookingId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete existing booking by its id. Only available for roles: admin, super admin")
    ResponseEntity<Boolean> deleteBooking(@RequestParam @NotNull(message = "Booking id is mandatory") UUID bookingId);
}

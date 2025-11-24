package edu.com.bookingsystem.api;


import edu.com.bookingsystem.dtos.BookingResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequestMapping(AuthApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with booking", description = AuthApi.API_PATH_DICTIONARY)
public interface BookingAPI {

    String API_PATH_DICTIONARY = "/api/book";

    @GetMapping("/")
    @Operation(summary = "Get own bookings. Available for all roles:")
    ResponseEntity<List<BookingResponseDTO>> getOwnBookingList(Principal user);

    @GetMapping("/{eventId}")
    @Operation(summary = "Get all bookings by event id. Available for admins")
    ResponseEntity<List<BookingResponseDTO>> getAllBookingForAdminByEventid(@PathVariable UUID eventId, Principal user);

    @PostMapping("/{eventId}")
    @Operation(summary = "Book the event. Available for users only")
    ResponseEntity<BookingResponseDTO> bookEvent(@PathVariable UUID eventId, Principal principal);

    @PutMapping("/unbook/{bookingId}")
    @Operation(summary = "Book the event. Available for users")
    ResponseEntity<BookingResponseDTO> unBookEvent(@PathVariable UUID bookingId, Principal principal);

    @DeleteMapping("/delete/{bookingId}")
    @Operation(summary = "Delete existing booking by its id. Only available for roles: admin, super admin")
    ResponseEntity<Boolean> deleteBooking(@RequestParam UUID bookingId, Principal principal);
}

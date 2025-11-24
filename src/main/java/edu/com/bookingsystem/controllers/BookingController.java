package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.BookingAPI;
import edu.com.bookingsystem.dtos.BookingResponseDTO;
import edu.com.bookingsystem.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
public class BookingController implements BookingAPI {

    private final BookingService bookingService;

    @Override
    public ResponseEntity<List<BookingResponseDTO>> getOwnBookingList(Principal user) {
        List<BookingResponseDTO> response = bookingService.getList(user);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<BookingResponseDTO>> getAllBookingsByEventId(UUID eventId) {
        List<BookingResponseDTO> response = bookingService.getListByEventId(eventId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BookingResponseDTO> bookEvent(UUID eventId, Principal user) {
        BookingResponseDTO response = bookingService.bookEvent(eventId, user);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BookingResponseDTO> unBookEvent(UUID bookingId, Principal user) {
        BookingResponseDTO response = bookingService.unBookEvent(bookingId, user);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Boolean> deleteBooking(UUID bookingId) {
        Boolean response = bookingService.deleteBooking(bookingId);
        return ResponseEntity.ok(response);
    }
}

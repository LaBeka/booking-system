package edu.com.bookingsystem.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Event ID is required")
    private UUID eventId;
}

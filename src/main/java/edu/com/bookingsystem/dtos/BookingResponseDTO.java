package edu.com.bookingsystem.dtos;

import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BookingResponseDTO {

    private UUID id;
    private UserResponseDTO bookedBy;
    private EventResponseDTO event;
    private LocalDateTime bookedOn;
    private boolean active;
}

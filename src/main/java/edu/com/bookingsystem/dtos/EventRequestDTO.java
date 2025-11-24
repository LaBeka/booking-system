package edu.com.bookingsystem.dtos;

import edu.com.bookingsystem.models.event.EventType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Event type is required")
    private EventType type;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Event date/time is required")
    private LocalDateTime when;

    @Min(value = 1, message = "Max participants must be at least 1")
    private int maxParticipants;

    @NotNull(message = "Admin ID is required")
    private UUID createdBy;
}

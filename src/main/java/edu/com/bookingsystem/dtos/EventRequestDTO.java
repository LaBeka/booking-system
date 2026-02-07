package edu.com.bookingsystem.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Event type is required")
    private String type;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Event date/time is required")
    @Future(message = "Event cannot be in the past")
    private LocalDateTime when;

    @Min(value = 1, message = "Max participants must be at least 1")
    private int maxParticipants;

}

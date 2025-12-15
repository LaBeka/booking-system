package edu.com.bookingsystem.dtos;

import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.models.event.EventType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {

    private UUID id;
    private String title;
    private String description;
    private EventType type;
    private boolean active;
    private boolean deprecated;
    private String location;
    private LocalDateTime when;
    private int maxParticipants;
    private int currentParticipants;
    private UserResponseDTO createdBy;
    private LocalDateTime createdAt;
    private OrganizationResDTO organization;
}

package edu.com.bookingsystem.dtos.user;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String fullName;
    private String email;
    private boolean active;
    private Set<String> roles; // names of roles
}

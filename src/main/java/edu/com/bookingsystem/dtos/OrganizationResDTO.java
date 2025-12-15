package edu.com.bookingsystem.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OrganizationResDTO {

    private UUID id;
    private boolean active;
    private boolean deprecated;
    private String name;
    private String email;
}

package edu.com.bookingsystem.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrganizationReqDTO {

    @NotBlank(message = "Organization name is required")
    private String name;

    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    private String email;

}

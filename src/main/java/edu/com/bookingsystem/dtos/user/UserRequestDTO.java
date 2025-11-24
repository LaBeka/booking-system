package edu.com.bookingsystem.dtos.user;

import edu.com.bookingsystem.models.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserRequestDTO {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private Set<Role> roles; // IS OPTIONAL
}

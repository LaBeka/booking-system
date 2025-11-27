package edu.com.bookingsystem.api;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;

@RequestMapping(AuthApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with AUTHENTICATION", description = AuthApi.API_PATH_DICTIONARY)
@Validated
public interface AuthApi {

    String API_PATH_DICTIONARY = "/api/auth";

    @PostMapping("/register/user")
    @Operation(summary = "Registration new user with roles: user")
    ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO dto, Principal principal);

    @PostMapping("/register/super_admin")
    @Operation(summary = "Registration new user with roles: user, admin, super admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER')")
    ResponseEntity<UserResponseDTO> registerSuperAdmin(@Valid @RequestBody UserRequestDTO dto, Principal principal);

    @PostMapping("/register/admin")
    @Operation(summary = "Registration new user with roles: user and admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER')")
    ResponseEntity<UserResponseDTO> registerAdmin(@Valid @RequestBody UserRequestDTO dto, Principal principal);

    @PostMapping("/login")
    @Operation(summary = "Create token for authentication to log in. FOR NON-GOOGLE LOGIN. NO ROLE REQUIRED")
    ResponseEntity<?> createAuthToken(
            @RequestParam @NotEmpty(message = "Email is mandatory")String email,
            @RequestParam @NotEmpty(message = "Password is mandatory") String password);


    @PostMapping("/refresh")
    @Operation(summary = "Refresh token for authentication to log in. NO ROLE REQUIRED")
    ResponseEntity<?> refreshAuthToken(
            @RequestParam @NotEmpty(message = "Email is mandatory") String refreshToken);
}

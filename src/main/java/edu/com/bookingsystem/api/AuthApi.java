package edu.com.bookingsystem.api;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.models.user.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;
import java.util.UUID;

@RequestMapping(AuthApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with AUTHENTICATION", description = AuthApi.API_PATH_DICTIONARY)
@Validated
public interface AuthApi {

    String API_PATH_DICTIONARY = "/api/auth";

    @PostMapping("/register/user")
    @Operation(summary = "Create new user with LOCALE.PROVIDER. With default role 'USER'")
    ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO userRequestDTO,
            @AuthenticationPrincipal CustomUserDetails principal);

    @PostMapping("/changeUserTo/admin")
    @Operation(summary = "Change existing user's 'USER' role to 'ADMIN'")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<UserResponseDTO> registerAdmin(
            @NotNull(message = "Email of the user is mandatory") @RequestParam String email,
            @AuthenticationPrincipal CustomUserDetails principal);

    @PostMapping("/changeUserTo/manager")
    @Operation(summary = "Change existing user's 'USER' role to 'MANAGER'")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<UserResponseDTO> registerManager(
            @NotNull(message = "Email of the user is mandatory") @RequestParam String email,
            @AuthenticationPrincipal CustomUserDetails principal);

    @PostMapping("/login")
    @Operation(summary = "Create token for authentication to log in. ONLY FOR NON-GOOGLE LOGIN. NO ROLE REQUIRED")
    ResponseEntity<?> login(
            @RequestParam @NotEmpty(message = "Email is mandatory") String email,
            @RequestParam @NotEmpty(message = "Password is mandatory") String password);


    @PostMapping("/refresh")
    @Operation(summary = "Refresh token for authentication to log in. NO ROLE REQUIRED")
    ResponseEntity<?> refreshAuthToken(
            @RequestParam @NotEmpty(message = "Access token is mandatory") String refreshToken);
}

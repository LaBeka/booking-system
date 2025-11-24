package edu.com.bookingsystem.api;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequestMapping(AuthApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with authentication", description = AuthApi.API_PATH_DICTIONARY)
@Validated
public interface AuthApi {

    String API_PATH_DICTIONARY = "/api/auth";

    @PostMapping("/register/user")
    @Operation(summary = "Registration new user with roles: user")
    ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO dto, Principal principal);

    @PostMapping("/register/super_admin")
    @Operation(summary = "Registration new user with roles: user, admin, super admin")
    ResponseEntity<UserResponseDTO> registerSuperAdmin(@Valid @RequestBody UserRequestDTO dto, Principal principal);

    @PostMapping("/register/admin")
    @Operation(summary = "Registration new user with roles: user and admin")
    ResponseEntity<UserResponseDTO> registerAdmin(@Valid @RequestBody UserRequestDTO dto, Principal principal);

    @PostMapping("/login")
    @Operation(summary = "Create token for authentication to log in")
    ResponseEntity<?> createAuthToken(
            @Valid @RequestParam @NotEmpty(message = "Email is mandatory")String email,
            @Valid @RequestParam @NotEmpty(message = "Password is mandatory") String password);
}

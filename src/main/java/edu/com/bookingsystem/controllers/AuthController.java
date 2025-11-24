package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.AuthApi;
import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<UserResponseDTO> registerUser(UserRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(authService.register(dto, List.of("USER"), principal.getName()));
    }

    @Override
    public ResponseEntity<UserResponseDTO> registerSuperAdmin(UserRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(authService.register(dto, List.of("ADMIN", "USER"), principal.getName()));

    }

    @Override
    public ResponseEntity<UserResponseDTO> registerAdmin(UserRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(authService.register(dto, List.of("SUPER_ADMIN", "ADMIN", "USER"), principal.getName()));

    }

    @Override
    public ResponseEntity<?> createAuthToken(String email, String password) {
        return null;
    }
}

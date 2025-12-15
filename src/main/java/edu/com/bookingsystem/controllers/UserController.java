package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.UserApi;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final AuthService authService;
    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllUser() {
        List<UserResponseDTO> response = authService.getAllUser();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllAdmin() {
        List<UserResponseDTO> response = authService.getAllAdmin();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllManagers(UUID orgId) {
        List<UserResponseDTO> response = authService.getAllManagers(orgId);
        return ResponseEntity.ok(response);
    }
}

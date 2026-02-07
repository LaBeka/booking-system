package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.UserApi;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.services.UserService;
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

    private final UserService userService;
    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllUser() {
        List<UserResponseDTO> response = userService.getAllUser();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllAdmin() {
        List<UserResponseDTO> response = userService.getAllAdmin();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllManagers(UUID orgId) {
        List<UserResponseDTO> response = userService.getAllManagers(orgId);
        return ResponseEntity.ok(response);
    }
}

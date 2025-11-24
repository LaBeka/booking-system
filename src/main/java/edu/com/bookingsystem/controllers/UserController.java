package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.UserApi;
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
public class UserController implements UserApi {

    private final AuthService authService;
    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllUser(Principal principal) {
        List<UserResponseDTO> response = authService.getAllUser(principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllAdmin(Principal principal) {
        List<UserResponseDTO> response = authService.getAllAdmin(principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllSuAdmin(Principal principal) {
        List<UserResponseDTO> response = authService.getAllSuperAdmin(principal.getName());
        return ResponseEntity.ok(response);
    }
}

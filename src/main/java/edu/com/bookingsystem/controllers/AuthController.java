package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.AuthApi;
import edu.com.bookingsystem.config.JwtUtil;
import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.models.user.CustomUserDetails;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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

    // TODO createAuthToken
    @Override
    public ResponseEntity<String> createAuthToken(String email, String password) {
        Authentication auth = null;
        try {
            auth = authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        UserAccount user  = ((CustomUserDetails) auth.getPrincipal()).getUser();

        return ResponseEntity.status(HttpStatus.OK).body(jwtUtil.generateToken(user));
    }
}

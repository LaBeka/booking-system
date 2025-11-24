package edu.com.bookingsystem.api;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequestMapping(AuthApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with authentication", description = AuthApi.API_PATH_DICTIONARY)
public interface AuthApi {

    String API_PATH_DICTIONARY = "/api/auth";

    @PostMapping("/register/user")
    @Operation(summary = "Registration new user with roles: user")
    ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO dto, Principal principal);

    @PostMapping("/register/super_admin")
    @Operation(summary = "Registration new user with roles: user, admin, super admin")
    ResponseEntity<UserResponseDTO> registerSuperAdmin(@RequestBody UserRequestDTO dto, Principal principal);

    @PostMapping("/register/admin")
    @Operation(summary = "Registration new user with roles: user and admin")
    ResponseEntity<UserResponseDTO> registerAdmin(@RequestBody UserRequestDTO dto, Principal principal);

    @PostMapping("/getAuth")
    @Operation(summary = "Create token for authentication")
    ResponseEntity<?> createAuthToken(@RequestParam String email, @RequestParam String password);
}

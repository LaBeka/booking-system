package edu.com.bookingsystem.api;

import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(UserApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with USER", description = UserApi.API_PATH_DICTIONARY)
public interface UserApi {

    String API_PATH_DICTIONARY = "/api/user";

    @GetMapping("/get/user")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "List of users with roles: user")
    ResponseEntity<List<UserResponseDTO>> getAllUser();

    @GetMapping("/get/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List of users with roles: admin")
    ResponseEntity<List<UserResponseDTO>> getAllAdmin();

    @GetMapping("/get/super-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List of users with roles: super admin")
    ResponseEntity<List<UserResponseDTO>> getAllSuAdmin();

}

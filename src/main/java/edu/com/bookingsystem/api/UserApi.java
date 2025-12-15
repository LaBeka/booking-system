package edu.com.bookingsystem.api;

import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping(UserApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with USER", description = UserApi.API_PATH_DICTIONARY)
public interface UserApi {

    String API_PATH_DICTIONARY = "/api/user";

    @GetMapping("/get/user")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "List of users with roles: user")
    ResponseEntity<List<UserResponseDTO>> getAllUser();

    @GetMapping("/get/admin")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "List of admins. Available for roles: manager")
    ResponseEntity<List<UserResponseDTO>> getAllAdmin();

    @GetMapping("/get/manager/{orgId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List of managers by org id. Available for roles: admin")
    ResponseEntity<List<UserResponseDTO>> getAllManagers(@PathVariable @NotNull(message = "Org id is mandatory") UUID orgId);

}

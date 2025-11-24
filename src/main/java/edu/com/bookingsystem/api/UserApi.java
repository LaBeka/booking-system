package edu.com.bookingsystem.api;


import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@RequestMapping(AuthApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with user", description = AuthApi.API_PATH_DICTIONARY)
public interface UserApi {

    String API_PATH_DICTIONARY = "/api/user";

    @GetMapping("/get/user")
    @Operation(summary = "List of users with roles: user")
    public ResponseEntity<List<UserResponseDTO>> getAllUser(Principal principal);

    @GetMapping("/get/admin")
    @Operation(summary = "List of users with roles: admin")
    public ResponseEntity<List<UserResponseDTO>> getAllAdmin(Principal principal);

    @GetMapping("/get/super-admin")
    @Operation(summary = "List of users with roles: super admin")
    public ResponseEntity<List<UserResponseDTO>> getAllSuAdmin(Principal principal);

}

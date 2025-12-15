package edu.com.bookingsystem.api;

import edu.com.bookingsystem.dtos.EventResponseDTO;
import edu.com.bookingsystem.dtos.OrganizationReqDTO;
import edu.com.bookingsystem.dtos.OrganizationResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequestMapping(OrganizationApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with ORGANIZATION", description = OrganizationApi.API_PATH_DICTIONARY)
@Validated
public interface OrganizationApi {
    String API_PATH_DICTIONARY = "/api/org";

    @GetMapping("/all")
    @Operation(summary = "Get all organizations. NO ROLES REQUIRED")
    ResponseEntity<List<OrganizationResDTO>> getList();

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new organization. Only available for roles: admin")
    ResponseEntity<OrganizationResDTO> createOrg(@Valid @RequestBody OrganizationReqDTO dto, Principal principal);

    @PutMapping("/update/{orgId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing organization. Only available for roles: admin")
    ResponseEntity<OrganizationResDTO> updateOrg(@PathVariable @NotNull(message = "Organization id is mandatory") UUID orgId, @Valid @RequestBody OrganizationReqDTO dto, Principal principal);

    @DeleteMapping("/delete/{orgId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete existing organization. Only available for roles: admin")
    ResponseEntity<Boolean> deleteOrg(@PathVariable @NotNull(message = "Organization id is mandatory") UUID orgId, Principal principal);



    @GetMapping("/upcoming")
    @Operation(summary = "Get all events by org id. NO ROLES REQUIRED")
    ResponseEntity<List<EventResponseDTO>> getAllUpcomingByOrg(
            @RequestParam
            @NotNull(message = "Organization id is mandatory")
            UUID orgId);

    @GetMapping("/past")
    @Operation(summary = "get all events by org id. NO ROLES REQUIRED")
    ResponseEntity<List<EventResponseDTO>> getAllPastByOrg(
            @RequestParam
            @NotNull(message = "Organization id is mandatory")
            UUID orgId);
}

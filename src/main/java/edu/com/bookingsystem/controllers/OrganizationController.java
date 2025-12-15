package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.OrganizationApi;
import edu.com.bookingsystem.dtos.EventResponseDTO;
import edu.com.bookingsystem.dtos.OrganizationReqDTO;
import edu.com.bookingsystem.dtos.OrganizationResDTO;
import edu.com.bookingsystem.services.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrganizationController implements OrganizationApi {

    private final OrganizationService orgService;

    @Override
    public ResponseEntity<List<OrganizationResDTO>> getList() {
        List<OrganizationResDTO> response = orgService.getAll();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OrganizationResDTO> createOrg(OrganizationReqDTO dto, Principal principal) {
        OrganizationResDTO response = orgService.createOrg(dto, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OrganizationResDTO> updateOrg(UUID orgId, OrganizationReqDTO dto, Principal principal) {
        OrganizationResDTO response = orgService.updateOrg(orgId, dto, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Boolean> deleteOrg(UUID orgId, Principal principal) {
        boolean response = orgService.deleteOrg(orgId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<EventResponseDTO>> getAllUpcomingByOrg(UUID orgId) {
        List<EventResponseDTO> response = orgService.getAllUpcomingByOrg(orgId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<EventResponseDTO>> getAllPastByOrg(UUID orgId) {
        List<EventResponseDTO> response = orgService.getAllPastByOrg(orgId);
        return ResponseEntity.ok(response);
    }
}

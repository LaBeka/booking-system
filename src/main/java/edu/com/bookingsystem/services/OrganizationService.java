package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.EventResponseDTO;
import edu.com.bookingsystem.dtos.OrganizationReqDTO;
import edu.com.bookingsystem.dtos.OrganizationResDTO;
import edu.com.bookingsystem.mappers.EventMapper;
import edu.com.bookingsystem.mappers.OrganizationMapper;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.EventRepo;
import edu.com.bookingsystem.repos.OrganizationRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepo organizationRepo;
    private final OrganizationMapper orgMapper;
    private final EventMapper eventMapper;
    private final EventRepo  eventRepo;
    private final UserAccountRepo userRepository;


    public List<OrganizationResDTO> getAll() {
        List<Organization> list = organizationRepo.findAll();
        return list.stream()
                .map(orgMapper::toDto)
                .toList();
    }

    public OrganizationResDTO createOrg(OrganizationReqDTO dto, String auth) {
        UserAccount admin = getAuthorizedUser(auth);
        organizationRepo.findByEmail(dto.getEmail()).orElseThrow(() ->
                new EntityExistsException("Organization by the given email already exists."));

        Organization savedOrg = organizationRepo.save(orgMapper.toEntity(dto));
        admin.setOrganization(savedOrg);
        userRepository.save(admin);
        return orgMapper.toDto(savedOrg);
    }

    public OrganizationResDTO updateOrg(UUID orgId, OrganizationReqDTO dto, String auth) {

        UserAccount admin = getAuthorizedUser(auth);
        Organization existingOrg = organizationRepo.findById(orgId).orElseThrow(() ->
                new EntityNotFoundException("Organization by the given id does not exist."));

        existingOrg.setName(dto.getName());
        existingOrg.setEmail(dto.getEmail());

        Organization savedOrg = organizationRepo.save(existingOrg);
        admin.setOrganization(savedOrg);
        userRepository.save(admin);
        return orgMapper.toDto(savedOrg);
    }

    public boolean deleteOrg(UUID orgId, String auth) {
        UserAccount admin = getAuthorizedUser(auth);
        Organization existingOrg = organizationRepo.findById(orgId).orElseThrow(() ->
                new EntityNotFoundException("Organization by the given id does not exist."));
        existingOrg.setActive(false);
        existingOrg.setDeprecated(true);

        organizationRepo.save(existingOrg);
        //TODO could be better to delete all cascade all users associated by the organization
        admin.setOrganization(null);
        userRepository.save(admin);
        return true;
    }

    public List<EventResponseDTO> getAllUpcomingByOrg(UUID orgId) {
        List<Event> list = eventRepo.findUpcomingListEventsByOrganization(orgId, LocalDateTime.now());
        return list.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventResponseDTO> getAllPastByOrg(UUID orgId) {
        List<Event> list = eventRepo.findPastListEventsByOrganization(orgId, LocalDateTime.now());
        return list.stream()
                .map(eventMapper::toDto)
                .toList();
    }


    private UserAccount getAuthorizedUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Logged ADMIN not found"));
    }
}

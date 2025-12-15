package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.EventRequestDTO;
import edu.com.bookingsystem.dtos.EventResponseDTO;
import edu.com.bookingsystem.exceptions.EntityDeactivatedException;
import edu.com.bookingsystem.exceptions.InvalidFieldValueException;
import edu.com.bookingsystem.exceptions.UnauthorizedException;
import edu.com.bookingsystem.mappers.EventMapper;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.event.EventType;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.EventRepo;
import edu.com.bookingsystem.repos.OrganizationRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceUnitTest {

    @Mock EventRepo repository;
    @Mock UserAccountRepo userRepository;
    @Mock EventMapper eventMapper;
    @Mock OrganizationRepo orgRepo;

    @Spy
    @InjectMocks
    EventService eventService;

    UserAccount creator;
    Organization org;
    EventRequestDTO request;
    EventResponseDTO response;
    Event eventEntity;

    @BeforeEach
    void setUp() {
        org = Organization.builder()
                .id(UUID.randomUUID())
                .name("Lexicon")
                .build();
        creator = UserAccount.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .organization(org)
                .build();
        request = EventRequestDTO.builder()
                .title("Test Event")
                .type("meetup")
                .build();
        eventEntity = Event.builder()
                .id(UUID.randomUUID())
                .title("string")
                .description("string")
                .location("string")
                .type(EventType.MEETUP)
                .active(true)
                .deprecated(false)
                .when(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .maxParticipants(10)
                .currentParticipants(5)
                .build();
        //mapstruct setup
        response = EventResponseDTO.builder()
                .id(eventEntity.getId())
                .title("Test Event")
                .build();
    }

    @AfterEach
    public void tearDown() {
        // Release test data after each test method
        repository.delete(eventEntity);
        userRepository.delete(creator);
        orgRepo.delete(org);
    }

    @Test
    @DisplayName("getExistingEventById should return Event entity")
    void getExistingEventById() {
        //arrange
        when(repository.findById(eventEntity.getId())).thenReturn(Optional.of(eventEntity));
        Event result = eventService.getExistingEventById(eventEntity.getId());
        assertEquals(result, eventEntity);
    }

    @Test
    @DisplayName("getExistingEventById should throw not found exception")
    void getExistingEventByIdNotFound() {
        when(repository.findById(eventEntity.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()->{
            eventService.getExistingEventById(eventEntity.getId());
        });
    }

    @Test
    @DisplayName("createEvent should return EventResponseDTO")
    void createEvent_Success() {
        // mock user
        when(userRepository.findByEmail(creator.getEmail()))
                .thenReturn(Optional.of(creator));

        // mock org
        when(orgRepo.findById(org.getId()))
                .thenReturn(Optional.of(org));

        // mock mapper
        when(eventMapper.toEntity(any(), any(), any(), any()))
                .thenReturn(eventEntity);

        // mock save
        when(repository.save(eventEntity))
                .thenReturn(eventEntity);

        // mock dto
        when(eventMapper.toDto(eventEntity))
                .thenReturn(response);

        EventResponseDTO result =
                eventService.createEvent(request, "admin@test.com");

        assertEquals(response.getId(), result.getId());
        assertEquals("Test Event", result.getTitle());

        verify(repository).save(eventEntity);
        verify(eventMapper).toDto(eventEntity);
    }

    @Test
    void createEvent_userNotFound() {
        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            eventService.createEvent(request, "admin@test.com");
        });
    }

    @Test
    void createEvent_organizationNotFound() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(creator));

        when(orgRepo.findById(org.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            eventService.createEvent(request, "admin@test.com");
        });
    }

    @Test
    void createEvent_invalidType() {
        request.setType("xxx-invalid-type");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(creator));

        when(orgRepo.findById(org.getId()))
                .thenReturn(Optional.of(org));

        assertThrows(InvalidFieldValueException.class, () -> {
            eventService.createEvent(request, "admin@test.com");
        });
    }

    @Test
    void getAvailableSeats() {
        when(repository.findById(eventEntity.getId())).thenReturn(Optional.of(eventEntity));
        int result = eventService.getAvailableSeats(eventEntity.getId());
        assertTrue(result >= 1);
    }

    @Test
    void updateEvent_success() {
        // ensure both user and event belong to SAME organization
        creator.setOrganization(org);
        eventEntity.setOrganization(org);

        // mock user
        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(creator));

        // mock event
        when(repository.findById(eventEntity.getId()))
                .thenReturn(Optional.of(eventEntity));

        // ensure event is active + not expired
        eventEntity.setActive(true);
        eventEntity.setWhen(LocalDateTime.now().plusDays(1));

        // mock mapper to return final DTO
        when(eventMapper.toDto(eventEntity)).thenReturn(response);

        EventResponseDTO result =
                eventService.updateEvent(eventEntity.getId(), request, "admin@test.com");

        assertEquals(response.getId(), result.getId());
        assertEquals("Test Event", result.getTitle());
        verify(repository).save(eventEntity);
    }

    @Test
    void updateEvent_orgMismatch() {
        // creator belongs to org A
        Organization orgA = Organization.builder().id(UUID.randomUUID()).build();
        creator.setOrganization(orgA);

        // event belongs to org B
        Organization orgB = Organization.builder().id(UUID.randomUUID()).build();
        eventEntity.setOrganization(orgB);

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(creator));

        when(repository.findById(eventEntity.getId()))
                .thenReturn(Optional.of(eventEntity));

        assertThrows(UnauthorizedException.class, () -> {
            eventService.updateEvent(eventEntity.getId(), request, "admin@test.com");
        });
    }

    @Test
    void updateEvent_expiredOrInactive() {
        creator.setOrganization(org);
        eventEntity.setOrganization(org);

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(creator));

        when(repository.findById(eventEntity.getId()))
                .thenReturn(Optional.of(eventEntity));

        // CASE 1: INACTIVE
        eventEntity.setActive(false);

        assertThrows(EntityDeactivatedException.class, () -> {
            eventService.updateEvent(eventEntity.getId(), request, "admin@test.com");
        });

        // CASE 2: EXPIRED
        eventEntity.setActive(true);
        eventEntity.setWhen(LocalDateTime.now().minusDays(1));

        assertThrows(EntityDeactivatedException.class, () -> {
            eventService.updateEvent(eventEntity.getId(), request, "admin@test.com");
        });
    }

    @Test
    void updateEvent_invalidType() {
        creator.setOrganization(org);
        eventEntity.setOrganization(org);

        request.setType("INVALIDXXXXX");

        when(userRepository.findByEmail(creator.getEmail()))
                .thenReturn(Optional.of(creator));

        when(repository.findById(eventEntity.getId()))
                .thenReturn(Optional.of(eventEntity));

        eventEntity.setWhen(LocalDateTime.now().plusDays(1));

        assertThrows(InvalidFieldValueException.class, () -> {
            eventService.updateEvent(eventEntity.getId(), request, "admin@test.com");
        });
    }

    @Test
    @DisplayName("deleteEvent should return true")
    void deleteEvent_success(){
        // Arrange (Given) mock user
        when(userRepository.findByEmail(creator.getEmail()))
                .thenReturn(Optional.of(creator));

        when(repository.findById(eventEntity.getId()))
                .thenReturn(Optional.of(eventEntity));
        // Act (When)
        boolean result = eventService.deleteEvent(eventEntity.getId(), "admin@test.com");

        // Arrange (Given)
        assertTrue(result);
        assertFalse(eventEntity.isActive());
        assertTrue(eventEntity.isDeprecated());
        // Verify repository interaction
        verify(repository, times(1)).save(eventEntity);
    }

    @Test
    @DisplayName("deleteEvent when event is in future should return false")
    void deleteEvent_shouldReturnFalse_whenEventIsNotExpired(){
        // Arrange (Given)
        eventEntity.setWhen(LocalDateTime.now().plusDays(1));
        when(userRepository.findByEmail(creator.getEmail()))
                .thenReturn(Optional.of(creator));

        when(repository.findById(eventEntity.getId()))
                .thenReturn(Optional.of(eventEntity));

        // Act (When)
        boolean result = eventService.deleteEvent(eventEntity.getId(), "admin@test.com");
        // Arrange (Given)
        assertFalse(result);
        assertTrue(eventEntity.isActive());
        assertFalse(eventEntity.isDeprecated());
    }

    @Test
    @DisplayName("deleteEvent should return false when event is not active")
    void deleteEvent_shouldReturnFalse_whenEventIsNotActive(){
        // Arrange (Given)
        eventEntity.setActive(false);
        when(userRepository.findByEmail(creator.getEmail()))
                .thenReturn(Optional.of(creator));

        when(repository.findById(eventEntity.getId()))
                .thenReturn(Optional.of(eventEntity));

        // Act (When)
        boolean result = eventService.deleteEvent(eventEntity.getId(), "admin@test.com");
        // Arrange (Given)
        assertFalse(result);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("deleteEvent should return false when already deprecated")
    void deleteEvent_shouldReturnFalse_whenAlreadyDeprecated() {
        // Arrange (Given)
        eventEntity.setDeprecated(true);

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(creator));

        when(repository.findById(eventEntity.getId()))
                .thenReturn(Optional.of(eventEntity));

        // Act (When)
        boolean result = eventService.deleteEvent(eventEntity.getId(), "admin@test.com");

        assertFalse(result);

        verify(repository, never()).save(any());
    }

}




















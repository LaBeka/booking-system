package edu.com.bookingsystem.mappers.event;

import edu.com.bookingsystem.dtos.EventRequestDTO;
import edu.com.bookingsystem.mappers.EventMapper;
import edu.com.bookingsystem.models.Booking;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.event.EventType;
import edu.com.bookingsystem.models.user.UserAccount;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class ReqDTOToEntityTest {

    private EventMapper mapper;
    private EventType type;
    private UserAccount createdBy;
    private Organization organization;
    LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 12, 25, 10, 0, 0);
    List<Booking> bookings;
    private final Validator validator;

    public ReqDTOToEntityTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(EventMapper.class);
        type = EventType.MEETUP;
        createdBy = UserAccount.builder().id(UUID.randomUUID()).email("user@org.com")
                .build();
        organization = Organization.builder().id(UUID.randomUUID()).build();
        bookings = List.of(new Booking(), new Booking());
    }
    @Test
    void shouldMapReqDTOToEntity(){
        EventRequestDTO dto = EventRequestDTO.builder()
                .title("title")
                .description("description")
                .type("MEETUP")
                .location("location")
                .when(FIXED_TIME.minusDays(10))
                .maxParticipants(10)
                .build();
        Event entity = mapper.toEntity(dto, type, createdBy,  organization);

        assertNotNull(entity);
        //default primitive fields:
        assertTrue(entity.isActive());
        assertFalse(entity.isDeprecated());

        assertNotNull(entity);
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getLocation(), dto.getLocation());

        assertEquals(entity.getType().name(), dto.getType());
        assertEquals(entity.getCreatedBy().getId(), createdBy.getId());
        assertEquals(entity.getMaxParticipants(), dto.getMaxParticipants());
        assertEquals(0, entity.getCurrentParticipants(), "Initial create entity is null by default");

        assertTrue(entity.getCurrentParticipants()<entity.getMaxParticipants());
    }
    @Test
    void shouldGenerateCreatedAtTimestamp() {
        EventRequestDTO dto = EventRequestDTO.builder().title("Test Event").build();
        LocalDateTime beforeTest = LocalDateTime.now().minusSeconds(1);

        Event entity = mapper.toEntity(dto, EventType.MEETUP, createdBy, organization);

        assertNotNull(entity.getCreatedAt(), "CreatedAt must be generated");

        LocalDateTime actual = entity.getCreatedAt();
        LocalDateTime now = LocalDateTime.now().plusSeconds(1); // Buffer for slow execution

        assertTrue(actual.isAfter(beforeTest), "Timestamp should be newer than the test start time");
        assertTrue(actual.isBefore(now), "Timestamp should not be in the future");
    }
    @Test
    void shouldFail_WhenDateInThePast(){
        EventRequestDTO dto = EventRequestDTO.builder().when(LocalDateTime.now().minusDays(1)).build();

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors");

        boolean hasFutureError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Event cannot be in the past"));
        assertTrue(hasFutureError);
    }
    @Test
    void shouldPass_WhenDateInTheFuture(){
        EventRequestDTO dto = EventRequestDTO.builder()
                .title("title")
                .description("description")
                .type("MEETUP")
                .location("location")
                .when(LocalDateTime.now().plusDays(1))
                .maxParticipants(10)
                .build();
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Should have NO validation errors");
    }
    @Test
    @Description("Should prioritize method argument EventType overRequest Dto String type field")
    void shouldTakeEventTypeOverStringType(){
        EventRequestDTO dto = EventRequestDTO.builder().type("WEBINAR").build();
        Event entity = mapper.toEntity(dto, EventType.MEETUP, createdBy,  organization);
        assertEquals(
                EventType.MEETUP,
                entity.getType(),
                "Mapper should use the explicit type argument, ignoring the DTO string");
    }

    @Test
    void shouldFail_MaxParticipantsIsLessThanOne(){
        EventRequestDTO dto = EventRequestDTO.builder().maxParticipants(0).build();
        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        boolean hasMinError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Max participants must be at least 1"));
        assertTrue(hasMinError);
    }
    @Test
    void shouldFail_RequiredFieldsAreBlank() {
        EventRequestDTO dto = EventRequestDTO.builder()
                .title("")         // BLANK
                .description("   ") //  WHITESPACE ONLY
                .location(null)    //  NULL
                .when(LocalDateTime.now().plusDays(1))
                .maxParticipants(10)
                .build();

        var violations = validator.validate(dto);

        assertTrue(violations.size() >= 3, "Should fail on all blank fields");
    }

    @Test
    void shouldReturnNullWhenInputNull(){
        Event entity = mapper.toEntity(null, type, createdBy, organization);

        assertNotNull(entity,"Should create Event OBJECT even if DTO is null" );

        assertEquals(createdBy, entity.getCreatedBy(), "CreatedBy should be set");
        assertEquals(organization, entity.getOrganization(), "Organization should be set");
        assertEquals(type, entity.getType(), "Type should be set");

        assertNull(entity.getTitle());
        assertNull(entity.getDescription());
    }
}

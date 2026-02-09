package edu.com.bookingsystem.mappers.event;

import edu.com.bookingsystem.dtos.EventResponseDTO;
import edu.com.bookingsystem.mappers.EventMapper;
import edu.com.bookingsystem.mappers.OrganizationMapper;
import edu.com.bookingsystem.mappers.RoleMapper;
import edu.com.bookingsystem.mappers.UserMapper;
import edu.com.bookingsystem.models.Booking;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.event.EventType;
import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EntityToResDTOTest {

    private EventMapper mapper;
    private UserAccount createdBy;
    private Organization organization;
    LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 12, 25, 10, 0, 0);
    List<Booking> bookings;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(EventMapper.class);
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);
        OrganizationMapper organizationMapper = Mappers.getMapper(OrganizationMapper.class);

        Role role = new Role(1L, "USER");
        createdBy = UserAccount.builder().id(UUID.randomUUID()).email("user@org.com")
                .roles(Set.of(role)).build();
        organization = Organization.builder().id(UUID.randomUUID()).build();
        bookings = List.of(new Booking(), new Booking());
        //IN @Component class EventMapperImpl implements EventMapper i have @Autowired user&org mappers
        ReflectionTestUtils.setField(mapper, "userMapper", userMapper);
        ReflectionTestUtils.setField(mapper, "organizationMapper", organizationMapper);

        // Inject dependencies into UserMapper, who needs OrganizationMapper to convert UserAccount.organization
        ReflectionTestUtils.setField(userMapper, "organizationMapper", organizationMapper);

        // Inject dependencies into UserMapper, who needs RoleMapper to convert UserAccount.roles
        RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);
        ReflectionTestUtils.setField(userMapper, "roleMapper", roleMapper);

    }

    @Test
    void shouldMapEntityToResDTO(){

        Event entity = Event.builder()
                .id(UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"))
                .title("Event title")
                .description("Event description")
                .type(EventType.MEETUP)
                .active(true)
                .deprecated(false)
                .location("location")
                .when(FIXED_TIME)
                .createdBy(createdBy)
                .maxParticipants(5)
                .bookings(bookings)
                .organization(organization)
                .build();
        EventResponseDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getType(), entity.getType());
        assertEquals(dto.isActive(), entity.isActive());
        assertEquals(dto.isDeprecated(), entity.isDeprecated());
        assertEquals(dto.getLocation(), entity.getLocation());
        assertEquals(dto.getWhen(), entity.getWhen());
        assertEquals(dto.getCreatedBy().getId(), createdBy.getId());
        assertEquals(dto.getMaxParticipants(), entity.getMaxParticipants());
        assertEquals(2, dto.getCurrentParticipants());
        assertEquals(dto.getOrganization().getId(), organization.getId());
        //default primitive fields:
        assertTrue(dto.isActive());
        assertFalse(dto.isDeprecated());
    }
    @Test
    void shouldReturnNullWhenInputNull(){
        assertNull(mapper.toDto(null), "Mapper should return null for null input.");
    }
    @Test
    void shouldMapCurrentParticipantToZERO_WhenBookingsListIsNull(){
        Event entity = Event.builder().id(UUID.randomUUID()).title("title")
                .bookings(null).build();
        EventResponseDTO dto = mapper.toDto(entity);
        assertNotNull(dto, "Mapper should not return null OBJECT.");
        assertEquals(0, dto.getCurrentParticipants(), "Should return 0 safely when list is null");
    }

}

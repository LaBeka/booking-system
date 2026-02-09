package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.event.EventType;
import edu.com.bookingsystem.models.user.UserAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class EventRepoJPATest {

    @Autowired private EventRepo eventRepo;

    @Autowired private OrganizationRepo orgRepo;

    @Autowired private UserAccountRepo userRepo;

    UserAccount user;
    Event eventEntity;
    String location;
    Organization org;
    Event eventEntity2;

    @BeforeEach
    public void setUp() {
        // Initialize test data before each test method
        org = Organization.builder()
                .id(UUID.randomUUID())
                .name("Lexicon")
                .build();
        org = orgRepo.save(org);
        user = UserAccount.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .organization(org)
                .build();
        user = userRepo.save(user);
        eventEntity = Event.builder()
                .id(UUID.randomUUID())
                .title("string")
                .description("string")
                .location("string")
                .type(EventType.MEETUP)
                .active(true)
                .deprecated(false)
                .when(LocalDateTime.now().plusDays(1))
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .maxParticipants(10)
                .currentParticipants(5)
                .organization(org)
                .build();
        eventEntity2 = Event.builder()
                .id(UUID.randomUUID())
                .title("Second entity")
                .description("eventEntity2.setTitle();")
                .location("string")
                .type(EventType.MEETUP)
                .active(true)
                .deprecated(false)
                .when(LocalDateTime.now().plusDays(1))
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .maxParticipants(3)
                .currentParticipants(3)
                .organization(org)
                .build();;
        eventRepo.save(eventEntity);
        eventRepo.save(eventEntity2);
        location = "string";
    }

    @AfterEach
    public void tearDown() {
        // Release test data after each test method
        eventRepo.delete(eventEntity);
        userRepo.delete(user);
        orgRepo.delete(org);
    }

    @Test
    @DisplayName("findUpcomingListEventsByLocation should return list of future events from now")
    void findUpcomingListEventsByLocation() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> result = eventRepo.findUpcomingListEventsByLocation(location, now);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Event::getWhen)
                .allMatch(date -> date.isAfter(now), "All events must be in the future");
    }

    @Test
    @DisplayName("findPastListEventsByLocation should return list of past events from now")
    void findPastListEventsByLocation() {
        LocalDateTime now = LocalDateTime.now();

        eventEntity2.setWhen(now.minusDays(2));
        eventRepo.save(eventEntity2);
        eventEntity.setWhen(now.minusDays(2));
        eventRepo.save(eventEntity);

        List<Event> result = eventRepo.findPastListEventsByLocation(location, now);
        assertThat(result).hasSize(2);

        assertThat(result).extracting(Event::getWhen)
                .allMatch(date -> date.isBefore(now), "All events must be in the future");
    }

    @Test
    @DisplayName("findUpcomingListEventsByOrganization should return list of future events from now of one organization")
    void findUpcomingListEventsByOrganization() {
        LocalDateTime now = LocalDateTime.now();

        List<Event> result = eventRepo.findUpcomingListEventsByOrganization(org.getId(), now);
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Event::getWhen)
                .allMatch(date -> date.isAfter(now), "All events must be in the future");
    }
    @Test
    @DisplayName("findUpcomingListEventsByOrganization should fail return list of future events from now of one organization")
    void findUpcomingListEventsByOrganization_failed() {
        LocalDateTime now = LocalDateTime.now();

        org = orgRepo.save(Organization.builder().id(UUID.randomUUID()).build());

        List<Event> result = eventRepo.findUpcomingListEventsByOrganization(org.getId(), now);
        assertNotEquals(2, result.size());
        assertThat(result).extracting(Event::getWhen)
                .allMatch(date -> date.isBefore(now), "should fail because all events must be in the future");
    }

    @Test
    @DisplayName("findPastListEventsByOrganization should return list of past events from now of one organization")
    void findPastListEventsByOrganization() {
        LocalDateTime now = LocalDateTime.now();

        eventEntity2.setWhen(now.minusDays(2));
        eventRepo.save(eventEntity2);
        eventEntity.setWhen(now.minusDays(2));
        eventRepo.save(eventEntity);

        List<Event> result = eventRepo.findPastListEventsByOrganization(
                org.getId(),
                now
        );
        assertThat(result).size().isEqualTo(2);
        assertThat(result).extracting(Event::getWhen)
                .allMatch(date ->
                        date.isBefore(now), "all events must be in the past");
    }

    @Test
    @DisplayName("findPastListEventsByOrganization should fail return list of future events from now of one organization")
    void findPastListEventsByOrganization_failed() {
        LocalDateTime now = LocalDateTime.now();

        eventEntity2.setWhen(now.minusDays(2));
        eventRepo.save(eventEntity2);
        eventEntity.setWhen(now.minusDays(2));
        eventRepo.save(eventEntity);
        org = orgRepo.save(Organization.builder().id(UUID.randomUUID()).build());

        List<Event> result = eventRepo.findPastListEventsByOrganization(org.getId(), now);
        assertNotEquals(2, result.size());
        assertThat(result).extracting(Event::getWhen)
                .allMatch(date ->
                        date.isBefore(now), "should fail because all events must be in the past");
    }

    @Test
    @DisplayName("findByIdForUpdate should find event and apply lock")
    void findByIdForUpdate() {
        Optional<Event> result = eventRepo.findByIdForUpdate(eventEntity.getId());

        assertTrue(result.isPresent());
        assertEquals(eventEntity.getId(), result.get().getId());
    }

}
package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.event.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepo extends JpaRepository<Event, UUID> {

    @Query("SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%')) AND e.when > :date")
    List<Event> findUpcomingListEventsByLocation(@Param("location") String city, @Param("date") LocalDateTime now);

    @Query("SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%')) AND e.when < :date")
    List<Event> findPastListEventsByLocation(@Param("location") String city, @Param("date") LocalDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :eventId")
    Optional<Event> findByIdForUpdate(@Param("eventId") UUID eventId);
    //is used only for booking an event, which tries to avoid the concurrent-multiple booking at the same time when event has the last spot

    @Query("SELECT e FROM Event e WHERE e.organization.id = :id AND e.when > :date")
    List<Event> findUpcomingListEventsByOrganization(@Param("id") UUID orgId, @Param("date") LocalDateTime now);


    @Query("SELECT e FROM Event e WHERE e.organization.id = :id AND e.when < :date")
    List<Event> findPastListEventsByOrganization(@Param("id") UUID orgId,  @Param("date") LocalDateTime now);
}

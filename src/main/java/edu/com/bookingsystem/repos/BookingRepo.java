package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepo extends JpaRepository<Booking, UUID> {

    @Query("select b from Booking b where b.bookedBy.id = :user and b.event.id = :event")
    Optional<Booking> findByUserAndEvent(@Param("user") UUID userId, @Param("event") UUID eventId);

    @Query("select b from Booking b where b.bookedBy.id = :user")
    List<Booking> findAllByUserId(@Param("user") UUID id);

    @Query("select b from Booking b where b.bookedBy.id = :user")
    Optional<Booking> findByUser(@Param("user") UUID userId);

    @Query("select b from Booking b JOIN b.event be WHERE be.active = :active AND be.when < :date AND b.bookedBy.id = :user")
    List<Booking> findAllUpcomingByUserId(@Param("user")UUID id, @Param("active") boolean active, @Param("date") LocalDateTime now);

    @Query("select b from Booking b JOIN b.event be WHERE be.when > :date AND b.bookedBy.id = :user")
    List<Booking> findAllPastByUserId(@Param("user")UUID id, @Param("date") LocalDateTime now);
}

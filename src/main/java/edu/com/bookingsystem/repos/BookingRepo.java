package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepo extends JpaRepository<Booking, UUID> {

    @Query("select b from Booking b where b.bookedBy = :user and b.event.id = :event")
    Optional<Booking> findByUserAndEvent(@Param("user") UUID userId, @Param("event") UUID eventId);

    @Query("select b from Booking b where b.bookedBy = :user")
    List<Booking> findAllByUserId(@Param("user") UUID id);
}

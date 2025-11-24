package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepo extends JpaRepository<Event, UUID> {
}

package edu.com.bookingsystem.models;

import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.user.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    @JdbcTypeCode(Types.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "booked_by", nullable = false)
    private UserAccount bookedBy;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @JoinColumn(name = "booked_on", nullable = false)
    private LocalDateTime bookedOn;

    private boolean active = true;
}

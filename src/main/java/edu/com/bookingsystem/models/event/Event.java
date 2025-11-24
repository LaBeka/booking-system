package edu.com.bookingsystem.models.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.com.bookingsystem.models.Booking;
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
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    @JdbcTypeCode(Types.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private EventType type;

    private boolean active = true;
    private boolean deprecated = false;
    private String location;

    @Column(name = "when_time")
    private LocalDateTime when;

    @ManyToOne
    @JoinColumn(name = "created_by_admin_id", nullable = false)
    @JsonIgnore
    private UserAccount createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(length = 100, name = "max_participants")
    private int maxParticipants;

    @Column(length = 100, name = "current_participants")
    private int currentParticipants;

    @OneToMany(mappedBy = "event")
    private List<Booking> bookings;
}

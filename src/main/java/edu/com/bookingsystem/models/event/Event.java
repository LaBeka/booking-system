package edu.com.bookingsystem.models.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.com.bookingsystem.models.Booking;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.user.UserAccount;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return active == event.active && deprecated == event.deprecated && maxParticipants == event.maxParticipants && currentParticipants == event.currentParticipants && Objects.equals(id, event.id) && Objects.equals(title, event.title) && Objects.equals(description, event.description) && type == event.type && Objects.equals(location, event.location) && Objects.equals(when, event.when) && Objects.equals(createdBy, event.createdBy) && Objects.equals(createdAt, event.createdAt) && Objects.equals(bookings, event.bookings) && Objects.equals(organization, event.organization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, type, active, deprecated, location, when, createdBy, createdAt, maxParticipants, currentParticipants, bookings, organization);
    }
}

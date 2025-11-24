package edu.com.bookingsystem.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_updates")
public class UserUpdate {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    @JdbcTypeCode(Types.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserAccount theUser;   // the user being updated

    @ManyToOne
    @JoinColumn(name = "updated_by_admin_id", nullable = false)
    @JsonIgnore
    private UserAccount updatedBy; // who did the update

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;  // timestamp of update

    @Column(name = "comment")
    private String comment;
}

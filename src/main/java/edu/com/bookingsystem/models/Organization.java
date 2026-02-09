package edu.com.bookingsystem.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import edu.com.bookingsystem.models.event.Event;
import edu.com.bookingsystem.models.user.UserAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    @JdbcTypeCode(Types.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    private boolean active = true;
    private boolean deprecated = false;

    private String name;

    @Email(message = "Email format is invalid")
    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "organization")
    @JsonBackReference
    private List<UserAccount> managers;

    @OneToMany(mappedBy = "organization")
    @JsonManagedReference
    private List<Event> events;
}

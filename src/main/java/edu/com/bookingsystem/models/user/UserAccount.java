package edu.com.bookingsystem.models.user;

import edu.com.bookingsystem.models.Booking;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;


import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserAccount {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    @JdbcTypeCode(Types.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    @Email(message = "Email format is invalid")
    private String email;

    private String password;

    private boolean active = true;
    private boolean deprecated = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;
}

package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.user.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JwtTokenRepo extends JpaRepository<JwtToken, UUID> {

    Optional<JwtToken> getByEmail(String email);
}

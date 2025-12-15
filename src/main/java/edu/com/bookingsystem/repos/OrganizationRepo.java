package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepo extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByEmail(String email);
}

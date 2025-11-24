package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String roleName);
}

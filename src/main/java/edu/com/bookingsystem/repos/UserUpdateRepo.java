package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.user.UserUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserUpdateRepo extends JpaRepository<UserUpdate, UUID> {
}

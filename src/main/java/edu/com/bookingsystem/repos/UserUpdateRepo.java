package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.user.UserUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserUpdateRepo extends JpaRepository<UserUpdate, UUID> {
}

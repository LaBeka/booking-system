package edu.com.bookingsystem.repos;

import edu.com.bookingsystem.models.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepo extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);

    @Query("SELECT u FROM UserAccount u JOIN u.roles r WHERE r.name = :keyword")
    List<UserAccount> getAllByRoles(@Param("keyword") String role);
}

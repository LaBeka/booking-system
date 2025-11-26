package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.mappers.UserMapper;
import edu.com.bookingsystem.models.user.AuthProvider;
import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.models.user.UserUpdate;
import edu.com.bookingsystem.repos.RoleRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import edu.com.bookingsystem.repos.UserUpdateRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserAccountRepo userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final UserUpdateRepo userUpdateRepo;
    private final RoleRepo roleRepo;

    public UserResponseDTO register(UserRequestDTO dto, List<String> roleName, String email) {
        UserAccount admin = getAuthorizedUser(email);

        userRepository.findByEmail(dto.getEmail())
                .ifPresent(u -> { throw new EntityExistsException("User with the email already exists"); });

        Set<Role> roles = roleName.stream()
                .map(r -> roleRepo.findByName(r.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Role not found " + r)))
                .collect(Collectors.toSet());
        dto.setRoles(roles);
        UserAccount user = userMapper.toEntity(
                dto,
                encoder
        );
        UserAccount saved = userRepository.save(user);
        Optional<UserUpdate> update = createHistoryRecord(saved, roleName.get(0), admin);

        return  userMapper.toResponseDTO(saved);
    }

    private Optional<UserUpdate> createHistoryRecord(UserAccount user, String role, UserAccount updatedBy) {

        UserUpdate updateUser = UserUpdate.builder()
                .theUser(user)
                .updatedBy(updatedBy)
                .updatedAt(LocalDate.now())
                .build();
        switch (role) {
            case "USER" -> {
                updateUser.setComment( "Initial user registration");
            }

            case "ADMIN" -> {
                updateUser.setComment( "Initial admin registration");
            }

            case "SUPER_ADMIN" -> {
                updateUser.setComment( "Initial super admin registration");
            }
        }
        Optional<UserUpdate> saveUpdate = Optional.of(userUpdateRepo.save(updateUser));
        return saveUpdate;
    }

    private UserAccount getAuthorizedUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Logged ADMIN not found"));
    }

    public List<UserResponseDTO> getAllUser() {
        List<UserAccount> list = userRepository.getAllByRoles("USER");
        return list.stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    public List<UserResponseDTO> getAllAdmin(){
        List<UserAccount> list = userRepository.getAllByRoles("ADMIN");
        return list.stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    public List<UserResponseDTO> getAllSuperAdmin(){
        List<UserAccount> list = userRepository.getAllByRoles("SUPER_ADMIN");
        return list.stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    public UserAccount findOrCreateByEmail(String email, String name) {
        UserAccount admin = getAuthorizedUser("admin1@school.com");

        Set<Role> userRole = roleRepo.findAll().stream()
                .filter(role -> role.getName().equalsIgnoreCase("user"))
                .collect(Collectors.toSet());

        Optional<UserAccount> user = userRepository.findByEmail(email);
        UserAccount saved = null;
        if(user.isEmpty()) {
            UserAccount u = UserAccount.builder()
                    .fullName(name)
                    .email(email)
                    .password(encoder.encode("pass"))
                    .active(true)
                    .deprecated(false)
                    .authProvider(AuthProvider.GOOGLE)
                    .roles(userRole)
                    .build();
            saved = userRepository.save(u);
            Optional<UserUpdate> update = createHistoryRecord(saved, "USER", admin);
        }
        saved = user.get();
        return saved;
    }
}

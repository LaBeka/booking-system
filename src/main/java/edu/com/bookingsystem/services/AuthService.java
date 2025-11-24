package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.exceptions.UnauthorizedException;
import edu.com.bookingsystem.mappers.UserMapper;
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
        createHistoryRecord(user, roleName.get(0), admin);

        UserAccount saved = userRepository.save(user);
        return  userMapper.toResponseDTO(saved);
    }

    private void createHistoryRecord(UserAccount user, String role, UserAccount updatedBy) {

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
        userUpdateRepo.save(updateUser);

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
}

package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.mappers.UserMapper;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.user.AuthProvider;
import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.models.user.UserUpdate;
import edu.com.bookingsystem.repos.OrganizationRepo;
import edu.com.bookingsystem.repos.RoleRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import edu.com.bookingsystem.repos.UserUpdateRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAccountRepo userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final UserUpdateRepo userUpdateRepo;
    private final RoleRepo roleRepo;
    private final OrganizationRepo organizationRepo;


    public UserResponseDTO registerManager(String email, UUID orgId, List<String> roleName, String adminEmail) {
        UserAccount admin = getAuthorizedUser(adminEmail);
        Organization org = organizationRepo.findById(orgId).orElseThrow(() ->
                new EntityNotFoundException("Before to change user's role to manager I need Organization id."));

        UserAccount manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user with the email: " + email));
        Set<Role> roles = roleName.stream()
                .map(r -> roleRepo.findByName(r.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Role not found " + r)))
                .collect(Collectors.toSet());

        manager.setOrganization(org);
        roles.forEach(role -> manager.getRoles().add(role));

        UserAccount saved = userRepository.save(manager);
        Optional<UserUpdate> update = createHistoryRecord(saved, "MANAGER", admin,
                "Update user's(" + email + ") role from user to manager: ");

        return  userMapper.toResponseDTO(saved);
    }

    public UserResponseDTO registerAdmin(String email, UUID orgId,  List<String> roleName, String adminEmail) {
        UserAccount updatedBy = getAuthorizedUser(adminEmail);
        Organization org = organizationRepo.findById(orgId).orElseThrow(() ->
                new EntityNotFoundException("Before to change user's role to manager I need Organization id."));

        UserAccount admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user with the email: " + email));
        Set<Role> roles = roleName.stream()
                .map(r -> roleRepo.findByName(r.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Role not found " + r)))
                .collect(Collectors.toSet());

        admin.setOrganization(org);
        roles.forEach(role -> admin.getRoles().add(role));

        UserAccount savedAdmin = userRepository.save(admin);
        Optional<UserUpdate> update = createHistoryRecord(savedAdmin, "ADMIN", updatedBy,
                "Update user's(" + email + ") role from user to admin: ");

        return  userMapper.toResponseDTO(savedAdmin);
    }

    private Optional<UserUpdate> createHistoryRecord(UserAccount user, String role, UserAccount updatedBy, String message) {

        UserUpdate updateUser = UserUpdate.builder()
                .theUser(user)
                .updatedBy(updatedBy)
                .updatedAt(LocalDate.now())
                .build();
        switch (role) {
            case "USER": updateUser.setComment(message + "[" + user.getEmail() + "]"); break;
            case "ADMIN" : updateUser.setComment(message + "[" + user.getEmail() + "]"); break;
            case "SUPER_ADMIN": updateUser.setComment( message + "[" + user.getEmail() + "]"); break;
            case "MANAGER": updateUser.setComment( message + "[" + user.getEmail() + "]"); break;
        }
        Optional<UserUpdate> saveUpdate = Optional.of(userUpdateRepo.save(updateUser));
        return saveUpdate;
    }

    public UserAccount findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found by given id: " + id));
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

    public List<UserResponseDTO> getAllManagers(UUID orgId){
        Organization existingOrg = organizationRepo.findById(orgId).orElseThrow(() ->
                new EntityNotFoundException("Could not find organization with the id "));
        List<UserAccount> list = userRepository.getAllByRoles("MANAGER");
        return list.stream()
                .map(userMapper::toResponseDTO)
                .filter(u -> u.getOrg().getId().equals(existingOrg.getId()))
                .toList();
    }

    public UserAccount findOrCreateByEmailGoogle(String email, String name) {
        UserAccount createdBy = getAuthorizedUser("admin1@school.com"); // STATIC ADMIN_CRETED_BY

        Set<Role> userRole = roleRepo.findAll().stream()
                .filter(role -> role.getName().equalsIgnoreCase("user"))
                .collect(Collectors.toSet());

        Optional<UserAccount> user = userRepository.findByEmail(email);
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
            UserAccount saved = userRepository.save(u);
            user = Optional.of(saved);
            Optional<UserUpdate> update = createHistoryRecord(saved, "USER", createdBy,
                    "Initial creation of new user with email '" + email + "' with role 'USER'");
        }
        return user.get();
    }

    public UserResponseDTO createNewUserLocale(UserRequestDTO dto, String auth) {
        UserAccount createdBy = getAuthorizedUser(auth);
        Set<Role> userRole = roleRepo.findAll().stream()
                .filter(role -> role.getName().equalsIgnoreCase("user"))
                .collect(Collectors.toSet());

        Optional<UserAccount> user = userRepository.findByEmail(dto.getEmail());
        if(user.isEmpty()) {
            UserAccount u = UserAccount.builder()
                    .fullName(dto.getFullName())
                    .email(dto.getEmail())
                    .password(encoder.encode(dto.getPassword()))
                    .active(true)
                    .deprecated(false)
                    .authProvider(AuthProvider.LOCAL)
                    .roles(userRole)
                    .build();
            UserAccount saved = userRepository.save(u);
            user = Optional.of(saved);
            Optional<UserUpdate> update = createHistoryRecord(saved, "USER", createdBy,
                    "Initial creation of new user  with email '" + dto.getEmail() + "'  role 'USER'");
        }
        return userMapper.toResponseDTO(user.get());
    }
}

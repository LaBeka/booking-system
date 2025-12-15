package edu.com.bookingsystem.services;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.mappers.UserMapper;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.OrganizationRepo;
import edu.com.bookingsystem.repos.RoleRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import edu.com.bookingsystem.repos.UserUpdateRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock UserAccountRepo userRepository;
    @Mock UserMapper userMapper;
    @Mock PasswordEncoder encoder;
    @Mock UserUpdateRepo userUpdateRepo;
    @Mock RoleRepo roleRepo;
    @Mock OrganizationRepo organizationRepo;

    @Spy
    @InjectMocks
    AuthService service;

    UserResponseDTO response;
    UserRequestDTO request;
    UserAccount user;
    Role role;
    Organization org;

    @BeforeEach
    void setUp() {

    }

    @Test
    void findById() {
    }
}
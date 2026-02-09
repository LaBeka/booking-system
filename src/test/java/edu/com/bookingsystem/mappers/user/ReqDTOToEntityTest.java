package edu.com.bookingsystem.mappers.user;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.mappers.UserMapper;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReqDTOToEntityTest {

    private UserMapper mapper;
    Role role ;
    Organization organization;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final Validator validator;

    public ReqDTOToEntityTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(UserMapper.class);
        role = new Role(1L, "ADMIN");
        organization = Organization.builder().id(UUID.randomUUID()).build();

    }
    @Test
    void shouldMapReqDTOToEntity(){
        Role userRole = new Role(2L, "USER");
        Set<Role> roles = Set.of(userRole, role);
        UserRequestDTO dto = UserRequestDTO.builder()
                .fullName("Full Name")
                .email("email@exe.com")
                .password("password")
                .roles(roles)
                .build();
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded_secret_123");

        UserAccount entity = mapper.toEntity(dto, passwordEncoder);

        assertNotNull(entity);
        assertEquals("encoded_secret_123", entity.getPassword());
        verify(passwordEncoder).encode(dto.getPassword());
        assertEquals("Full Name", entity.getFullName());
        assertEquals("email@exe.com", entity.getEmail());

        assertTrue(entity.isActive(), "Mapper should set active to true constant");
        assertFalse(entity.isDeprecated(), "Mapper should set deprecated to false constant");

        assertNull(entity.getId(), "ID should be null/ignored");
        assertNull(entity.getBookings(), "Bookings should be null/ignored");

        assertEquals(roles, entity.getRoles());
    }
    @Test
    void shouldReturnNull_WhenDTOIsNull(){
        assertNull(mapper.toEntity(null,passwordEncoder), "Mapper should return null if input DTO is null");
    }
    @Test
    void shouldFail_RequiredFieldsAreBlank() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .fullName("")
                .email("   ")
                .password(null)
                .build();
        var violations = validator.validate(dto);
        assertTrue(violations.size() >= 3, "Should fail on all blank fields");
    }

    @Test
    void shouldFail_InvalidEmail() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("abcd")
                .build();
        var violations = validator.validate(dto);
        boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email format is invalid"));
        assertTrue(hasEmailError);
    }
    @Test
    void shouldFail_BlancPassword() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .password("")
                .build();
        var violations = validator.validate(dto);
        boolean hasPassError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password is required"));
        assertTrue(hasPassError);
    }
    @Test
    void shouldFail_ShortPassword() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .password("abc")
                .build();
        var violations = validator.validate(dto);
        boolean hasPassError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password must be at least 6 characters long"));
        assertTrue(hasPassError);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "password123!",  // Missing Uppercase
            "PASSWORD123!",  // Missing Lowercase
            "Password!!!!",  // Missing Digit
            "Password1234",  // Missing Special Char
            "password",      // Missing Everything
    })
    void shouldFail_WhenPasswordPatternIsInvalid(String badPassword) {
        UserRequestDTO dto = UserRequestDTO.builder()
                .password(badPassword)
                .build();
        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Expected validation error for password: " + badPassword);
        boolean hasPassError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password must contain upper, lower, digit, and special character(like: '@$!%*?&')"));
        assertTrue(hasPassError);
    }
    @Test
    void shouldPass_WhenPasswordIsValid() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("StrongP@ssw0rd") // <--- Meets ALL criteria
                .build();

        var violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Should have NO errors for a valid password");

    }
}

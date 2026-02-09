package edu.com.bookingsystem.mappers.user;

import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.mappers.OrganizationMapper;
import edu.com.bookingsystem.mappers.RoleMapper;
import edu.com.bookingsystem.mappers.UserMapper;
import edu.com.bookingsystem.models.Organization;
import edu.com.bookingsystem.models.user.AuthProvider;
import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EntityToResDTOTest {

    private UserMapper mapper;
    Role role;
    Organization org;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(UserMapper.class);
        role = new Role(1L, "ADMIN");
        org = Organization.builder().id(UUID.randomUUID()).build();
        OrganizationMapper orgMapper = Mappers.getMapper(OrganizationMapper.class);
        RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);
        ReflectionTestUtils.setField(mapper, "organizationMapper", orgMapper);
        ReflectionTestUtils.setField(mapper, "roleMapper", roleMapper);
    }
    @Test
    void shouldMapUserWithRole(){

        UserAccount entity = UserAccount.builder().roles(Set.of(role)).build();
        UserResponseDTO dto = mapper.toResponseDTO(entity);
        assertNotNull(dto);
        assertTrue(dto.getRoles().contains("ADMIN"));
    }
    @Test
    void successMapEntityToResponseDTO(){

        UserAccount entity = UserAccount.builder()
                .id(UUID.randomUUID())
                .fullName("fullName")
                .email("email@exe.com")
                .active(true)
                .deprecated(false)
                .authProvider(AuthProvider.GOOGLE)
                .roles(Set.of(role))
                .organization(org)
                .build();
        UserResponseDTO dto = mapper.toResponseDTO(entity);

        assertNotNull(dto);
        assertTrue(dto.getRoles().contains("ADMIN"));
        assertEquals("fullName", dto.getFullName());
        assertEquals("email@exe.com", dto.getEmail());
        assertTrue(dto.isActive());
        assertEquals(org.getId(), dto.getOrg().getId());

        assertTrue(dto.isActive());
    }
    @Test
    void shouldReturnNullWhenInputNull(){
        assertNull(mapper.toResponseDTO(null), "Mapper should return null for null input.");
    }

    @Test
    void shouldHandleNullFields(){
        UserAccount entity = UserAccount.builder().build();
        UserResponseDTO dto = mapper.toResponseDTO(entity);
        assertNotNull(dto);
        assertFalse(dto.isActive(), "Active should be false");
        assertFalse(entity.isDeprecated(), "Deprecated should be false");
    }
}

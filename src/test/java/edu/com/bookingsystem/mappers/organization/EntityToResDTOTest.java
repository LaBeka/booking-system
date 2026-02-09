package edu.com.bookingsystem.mappers.organization;

import edu.com.bookingsystem.dtos.OrganizationResDTO;
import edu.com.bookingsystem.mappers.OrganizationMapper;
import edu.com.bookingsystem.models.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class EntityToResDTOTest {
    private final OrganizationMapper mapper = Mappers.getMapper(OrganizationMapper.class);

    @Test
    void shouldMapEntityToResDto(){
        Organization entity =  Organization.builder()
                .active(true)
                .deprecated(false)
                .name("Org Name")
                .email("org@org.com")
                .build();

        OrganizationResDTO dto = mapper.toDto(entity);
        assertEquals("Org Name", dto.getName());
        assertEquals("org@org.com", dto.getEmail());
        assertTrue(dto.isActive());
        assertFalse(dto.isDeprecated());
    }

    @Test
    void shouldReturnNullWhenInputIsNull(){
        OrganizationResDTO dto = mapper.toDto(null);
        assertNull(dto, "Mapper should return null OBJECT if input is null");
    }

    @Test
    void shouldHandleNullFields(){
        Organization entity =  Organization.builder().build();
        OrganizationResDTO dto = mapper.toDto(entity);
        assertNotNull(dto, "The DTO OBJECT itself should not be null");
        assertNull(dto.getName(), "Name should be null");
        assertNull(dto.getEmail(), "Email should be null");
        assertNull(dto.getId(), "Id should be null");

        assertFalse(dto.isActive(), "Active should be false");
        assertFalse(dto.isDeprecated(), "Deprecated should be false");
    }
}


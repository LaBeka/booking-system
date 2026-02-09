package edu.com.bookingsystem.mappers.organization;

import edu.com.bookingsystem.dtos.OrganizationReqDTO;
import edu.com.bookingsystem.mappers.OrganizationMapper;
import edu.com.bookingsystem.models.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class ReqDTOToEntityTest {
    private final OrganizationMapper mapper = Mappers.getMapper(OrganizationMapper.class);

    @Test
    void shouldMapReqDTOToEntityWithDefaults(){
        OrganizationReqDTO dto = OrganizationReqDTO.builder()
                .name("Org Name")
                .email("org@org.com")
                .build();
        Organization entity = mapper.toEntity(dto);

        assertEquals("Org Name", entity.getName());
        assertEquals("org@org.com", entity.getEmail());

        assertTrue(entity.isActive(), "Active must be TRUE by default");
        assertFalse(entity.isDeprecated(), "Deprecated must be FALSE by default");
    }
    @Test
    void shouldReturnNullWhenInputIsNull(){
        assertNull(mapper.toEntity(null));
    }
    @Test
    void shouldHandleNullFields(){
        OrganizationReqDTO dto = OrganizationReqDTO.builder().build();
        Organization entity = mapper.toEntity(dto);
        assertNull(entity.getId(), "Id should be null");
        assertNull(entity.getName(), "Name should be null");
        assertNull(entity.getEmail(), "Email should be null");

        // CRITICAL: Constants must STILL be applied!
        assertTrue(entity.isActive(), "Active should be TRUE by default");
        assertFalse(entity.isDeprecated(), "Deprecated should be FALSE by default");
    }

}

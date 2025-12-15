package edu.com.bookingsystem.mappers;

import edu.com.bookingsystem.dtos.OrganizationReqDTO;
import edu.com.bookingsystem.dtos.OrganizationResDTO;
import edu.com.bookingsystem.models.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    @Mapping(target = "active", constant = "true")
    @Mapping(target = "deprecated", constant = "false")
    Organization toEntity(OrganizationReqDTO dto);

    OrganizationResDTO toDto(Organization entity);
}

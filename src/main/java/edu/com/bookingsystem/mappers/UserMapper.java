package edu.com.bookingsystem.mappers;

import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.models.user.UserAccount;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "deprecated", constant = "false")
    @Mapping(target = "password", expression = "java(encoder.encode(dto.getPassword()))")
    @Mapping(target = "roles", source = "dto.roles")
    @Mapping(target = "bookings", ignore = true)
    UserAccount toEntity(UserRequestDTO dto,
                  @Context PasswordEncoder encoder);

    @Mapping(target = "roles", expression = "java(entity.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()))")
    UserResponseDTO toResponseDTO(UserAccount entity);
}

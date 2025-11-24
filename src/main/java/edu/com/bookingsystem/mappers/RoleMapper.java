package edu.com.bookingsystem.mappers;

import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.repos.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {

    @Autowired
    protected RoleRepo roleRepository;

    // from role name → Role entity
    public Role map(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }

    // from Role entity → role name
    public String map(Role role) {
        return role.getName();
    }
}
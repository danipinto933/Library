package com.library.userservice.mapper;

import com.library.userservice.dto.RoleDto;
import com.library.userservice.model.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-17T18:42:29+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDto toDto(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleDto.RoleDtoBuilder roleDto = RoleDto.builder();

        roleDto.id( role.getId() );
        roleDto.role( role.getRole() );

        return roleDto.build();
    }

    @Override
    public Role toEntity(RoleDto roleDto) {
        if ( roleDto == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.id( roleDto.getId() );
        role.role( roleDto.getRole() );

        return role.build();
    }

    @Override
    public void updateEntityFromDto(RoleDto roleDto, Role role) {
        if ( roleDto == null ) {
            return;
        }

        role.setId( roleDto.getId() );
        role.setRole( roleDto.getRole() );
    }
}

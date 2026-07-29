package com.library.userservice.mapper;

import com.library.userservice.dto.RoleDto;
import com.library.userservice.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role role);

    Role toEntity(RoleDto roleDto);

    void updateEntityFromDto(RoleDto roleDto, @MappingTarget Role role);
}

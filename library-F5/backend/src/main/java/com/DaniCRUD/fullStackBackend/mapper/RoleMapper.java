package com.DaniCRUD.fullStackBackend.mapper;

import org.springframework.stereotype.Component;

import com.DaniCRUD.fullStackBackend.dto.response.RoleDto;
import com.DaniCRUD.fullStackBackend.model.Role;

@Component
public class RoleMapper
{
    public RoleDto toDto (Role role)
    {
        if (role == null) return null;

        return RoleDto.builder()
        .id(role.getId())
        .role(role.getRole())
        .build();
    }

    public Role toEntity (RoleDto roleDto)
    {
        if (roleDto == null) return null;

        return Role.builder()
        .id(roleDto.getId())
        .role(roleDto.getRole())
        .build();
    }

    public void updateEntityFromDto (RoleDto dto, Role role)
    {
        if (dto.getRole() != null)  role.setRole(dto.getRole());
    }
}

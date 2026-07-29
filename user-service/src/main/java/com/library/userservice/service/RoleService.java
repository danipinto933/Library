package com.library.userservice.service;

import com.library.userservice.dto.RoleDto;
import com.library.userservice.model.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleService {
    ResponseEntity<Role> addRole(RoleDto roleDto);
    ResponseEntity<List<RoleDto>> findAllRoles();
    Role findRoleByIdRole(Long id);
    Role findRoleByRoleName(String roleName);
    ResponseEntity<Role> updateRole(Long id, RoleDto rolDto);
    String deleteRole(Long id);
}

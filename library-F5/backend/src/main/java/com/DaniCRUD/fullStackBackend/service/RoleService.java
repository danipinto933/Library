package com.DaniCRUD.fullStackBackend.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.DaniCRUD.fullStackBackend.dto.response.RoleDto;
import com.DaniCRUD.fullStackBackend.model.Role;

@Service
public interface RoleService 
{
    ResponseEntity<Role> addRole (RoleDto roleDto);

    ResponseEntity<List<RoleDto>> findAllRoles();
    Role findRoleByIdRole(Long id);
    Role findRoleByRoleName(String roleName);
    
    ResponseEntity<Role> updateRole (Long id, RoleDto rolDto);

    String deleteRole (Long id);
}

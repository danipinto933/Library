package com.library.userservice.service;

import com.library.userservice.dto.RoleDto;
import com.library.userservice.exception.ResourceNotFoundException;
import com.library.userservice.mapper.RoleMapper;
import com.library.userservice.model.Role;
import com.library.userservice.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public ResponseEntity<Role> addRole(RoleDto roleDto) {
        Role role = roleMapper.toEntity(roleDto);
        if (role == null) {
            throw new ResourceNotFoundException("Rol no encontrado");
        }
        roleRepository.save(role);
        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<RoleDto>> findAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDto> rolesDtos = roles.stream()
                .map(roleMapper::toDto)
                .toList();
        return new ResponseEntity<>(rolesDtos, HttpStatus.OK);
    }

    @Override
    public Role findRoleByIdRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol con el id " + id + " no encontrado"));
    }

    @Override
    public Role findRoleByRoleName(String roleName) {
        Role role = roleRepository.findByRole(roleName);
        if (role == null) {
            throw new ResourceNotFoundException("Rol con el nombre " + roleName + " no encontrado");
        }
        return role;
    }

    @Override
    public ResponseEntity<Role> updateRole(Long id, RoleDto rolDto) {
        Role olderRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El rol no se ha encontrado"));
        roleMapper.updateEntityFromDto(rolDto, olderRole);
        Role updatedRole = roleRepository.save(olderRole);
        return new ResponseEntity<>(updatedRole, HttpStatus.OK);
    }

    @Override
    public String deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El rol no se ha encontrado"));
        roleRepository.delete(role);
        return "Rol eliminado correctamente";
    }
}

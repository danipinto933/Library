package com.DaniCRUD.fullStackBackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.DaniCRUD.fullStackBackend.dto.response.RoleDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.mapper.RoleMapper;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleServiceImpl;

    private Role role1;
    private Role role2;
    private RoleDto roleDto1;

    @BeforeEach
    void setup() {
        role1 = Role.builder().id(1L).role("ROLE_USER").build();
        role2 = Role.builder().id(2L).role("ROLE_ADMIN").build();
        roleDto1 = RoleDto.builder().id(1L).role("ROLE_USER").build();
    }

    @Test
    void testSaveRole() {
        given(roleRepository.save(any(Role.class))).willReturn(role1);

        ResponseEntity<Role> response = roleServiceImpl.addRole(roleDto1);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("ROLE_USER", response.getBody().getRole());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testSaveRole_NullMappedEntity() {
        // Simular que el mapeador devuelve null
        given(roleMapper.toEntity(any(RoleDto.class))).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> roleServiceImpl.addRole(roleDto1));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testFindAllRoles() {
        given(roleRepository.findAll()).willReturn(List.of(role1, role2));

        ResponseEntity<List<RoleDto>> response = roleServiceImpl.findAllRoles();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testFindAllRoles_EmptyList() {
        given(roleRepository.findAll()).willReturn(Collections.emptyList());

        ResponseEntity<List<RoleDto>> response = roleServiceImpl.findAllRoles();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testFindRoleById() {
        given(roleRepository.findById(1L)).willReturn(Optional.of(role1));

        Role foundRole = roleServiceImpl.findRoleByIdRole(1L);

        assertNotNull(foundRole);
        assertEquals("ROLE_USER", foundRole.getRole());
    }

    @Test
    void testFindRoleById_NotFound() {
        given(roleRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleServiceImpl.findRoleByIdRole(999L));
    }

    @Test
    void testFindRoleByRoleName() {
        given(roleRepository.findByRole("ROLE_USER")).willReturn(role1);

        Role foundRole = roleServiceImpl.findRoleByRoleName("ROLE_USER");

        assertNotNull(foundRole);
        assertEquals("ROLE_USER", foundRole.getRole());
    }

    @Test
    void testFindRoleByRoleName_NotFound() {
        given(roleRepository.findByRole("ROLE_INEXISTENT")).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> roleServiceImpl.findRoleByRoleName("ROLE_INEXISTENT"));
    }

    @Test
    void testUpdateRole() {
        given(roleRepository.findById(1L)).willReturn(Optional.of(role1));
        given(roleRepository.save(any(Role.class))).willReturn(role1);

        ResponseEntity<Role> response = roleServiceImpl.updateRole(1L, roleDto1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testUpdateRole_NotFound() {
        given(roleRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleServiceImpl.updateRole(999L, roleDto1));
    }

    @Test
    void testDeleteRole() {
        given(roleRepository.findById(1L)).willReturn(Optional.of(role1));

        String result = roleServiceImpl.deleteRole(1L);

        assertEquals("Rol eliminado correctamente", result);
        verify(roleRepository).delete(role1);
    }

    @Test
    void testDeleteRole_NotFound() {
        given(roleRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleServiceImpl.deleteRole(999L));
    }
}

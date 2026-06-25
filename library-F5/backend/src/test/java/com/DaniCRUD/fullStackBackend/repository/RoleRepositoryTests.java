package com.DaniCRUD.fullStackBackend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.DaniCRUD.fullStackBackend.model.Role;

@DataJpaTest
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    private Role role1;
    private Role role2;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests de repository de Role desde cero...");
        role1 = Role.builder().role("ROLE_USER").build();
        role2 = Role.builder().role("ROLE_ADMIN").build();
    }

    @Test
    void testSaveRole() {
        Role savedRole = roleRepository.save(role1);
        assertNotNull(savedRole);
        assertNotNull(savedRole.getId());
        assertEquals("ROLE_USER", savedRole.getRole());
    }

    @Test
    void testFindAllRoles() {
        roleRepository.save(role1);
        roleRepository.save(role2);

        List<Role> roles = roleRepository.findAll();
        assertNotNull(roles);
        assertTrue(roles.size() >= 2);
    }

    @Test
    void testFindRoleById() {
        Role savedRole = roleRepository.save(role1);
        Role foundRole = roleRepository.findById(savedRole.getId()).orElse(null);

        assertNotNull(foundRole);
        assertEquals(savedRole.getId(), foundRole.getId());
    }

    @Test
    void testFindByRole() {
        roleRepository.save(role1);

        Role foundRole = roleRepository.findByRole("ROLE_USER");
        assertNotNull(foundRole);
        assertEquals("ROLE_USER", foundRole.getRole());
    }

    @Test
    void testFindByRole_NotFound() {
        Role foundRole = roleRepository.findByRole("ROLE_NON_EXISTENT");
        assertNull(foundRole);
    }

    @Test
    void testUpdateRole() {
        Role savedRole = roleRepository.save(role1);
        savedRole.setRole("ROLE_MODERATOR");

        Role updatedRole = roleRepository.save(savedRole);
        assertNotNull(updatedRole);
        assertEquals("ROLE_MODERATOR", updatedRole.getRole());
    }

    @Test
    void testDeleteRole() {
        Role savedRole = roleRepository.save(role1);
        assertNotNull(savedRole);

        roleRepository.delete(savedRole);
        Role foundRole = roleRepository.findById(savedRole.getId()).orElse(null);
        assertNull(foundRole);
    }
}

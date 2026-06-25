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
import com.DaniCRUD.fullStackBackend.model.User;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role role1;
    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests de repository de User desde cero...");
        
        role1 = Role.builder().role("ROLE_USER").build();
        roleRepository.save(role1);

        user1 = User.builder()
                .userName("user1")
                .name("User One")
                .email("user1@example.com")
                .password("password123")
                .role(role1)
                .build();

        user2 = User.builder()
                .userName("user2")
                .name("User Two")
                .email("user2@example.com")
                .password("password456")
                .role(role1)
                .build();
    }

    @Test
    void testSaveUser() {
        User savedUser = userRepository.save(user1);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("user1", savedUser.getUserName());
    }

    @Test
    void testFindAllUsers() {
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();
        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }

    @Test
    void testFindUserById() {
        User savedUser = userRepository.save(user1);
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
    }

    @Test
    void testFindByUserName() {
        userRepository.save(user1);

        User foundUser = userRepository.findByUserName("user1");
        assertNotNull(foundUser);
        assertEquals("user1", foundUser.getUserName());
    }

    @Test
    void testFindByUserName_NotFound() {
        User foundUser = userRepository.findByUserName("non_existent");
        assertNull(foundUser);
    }

    @Test
    void testFindUserByName() {
        userRepository.save(user1);

        User foundUser = userRepository.findUserByName("User One");
        assertNotNull(foundUser);
        assertEquals("User One", foundUser.getName());
    }

    @Test
    void testFindUserByName_NotFound() {
        User foundUser = userRepository.findUserByName("Inexistente");
        assertNull(foundUser);
    }

    @Test
    void testFindUserByEmail() {
        userRepository.save(user1);

        User foundUser = userRepository.findUserByEmail("user1@example.com");
        assertNotNull(foundUser);
        assertEquals("user1@example.com", foundUser.getEmail());
    }

    @Test
    void testFindUserByEmail_NotFound() {
        User foundUser = userRepository.findUserByEmail("inexistente@example.com");
        assertNull(foundUser);
    }

    @Test
    void testFindUsersByRole() {
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findUsersByRole(role1);
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void testFindUsersByRole_Empty() {
        Role role2 = Role.builder().role("ROLE_ADMIN").build();
        roleRepository.save(role2);

        List<User> users = userRepository.findUsersByRole(role2);
        assertTrue(users.isEmpty());
    }

    @Test
    void testUpdateUser() {
        User savedUser = userRepository.save(user1);
        savedUser.setName("User One Updated");

        User updatedUser = userRepository.save(savedUser);
        assertNotNull(updatedUser);
        assertEquals("User One Updated", updatedUser.getName());
    }

    @Test
    void testDeleteUser() {
        User savedUser = userRepository.save(user1);
        assertNotNull(savedUser);

        userRepository.delete(savedUser);
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNull(foundUser);
    }
}

package com.DaniCRUD.fullStackBackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.DaniCRUD.fullStackBackend.dto.response.UserDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.exception.UserAlreadyExistsException;
import com.DaniCRUD.fullStackBackend.mapper.UserMapper;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleServiceImpl roleServiceImpl;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private Role role1;

    @BeforeEach
    void setup() {
        role1 = Role.builder().id(2L).role("ROLE_USER").build();

        user1 = User.builder()
                .id(1L)
                .userName("user1")
                .name("User One")
                .email("user1@example.com")
                .password("password123")
                .role(role1)
                .reserves(new ArrayList<>())
                .build();

        user2 = User.builder()
                .id(2L)
                .userName("user2")
                .name("User Two")
                .email("user2@example.com")
                .password("password456")
                .role(role1)
                .reserves(new ArrayList<>())
                .build();

        userDto1 = UserDto.builder()
                .id(1L)
                .userName("user1")
                .name("User One")
                .email("user1@example.com")
                .password("password123")
                .role("ROLE_USER")
                .build();

        userDto2 = UserDto.builder()
                .id(2L)
                .userName("user2")
                .name("User Two")
                .email("user2@example.com")
                .password("password456")
                .role("ROLE_USER")
                .build();
    }

    @Test
    void testAddUser() {
        given(userMapper.toEntity(any(UserDto.class))).willReturn(user1);
        given(userRepository.findByUserName("user1")).willReturn(null);
        given(userRepository.findUserByEmail("user1@example.com")).willReturn(null);
        given(userRepository.findUserByName("User One")).willReturn(null);
        given(roleServiceImpl.findRoleByIdRole(2L)).willReturn(role1);
        given(bcrypt.encode("password123")).willReturn("encryptedPassword");
        given(userRepository.save(any(User.class))).willReturn(user1);

        ResponseEntity<User> response = userServiceImpl.addUser(userDto1);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("user1", response.getBody().getUserName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testAddUser_UserNameAlreadyExists() {
        given(userMapper.toEntity(any(UserDto.class))).willReturn(user1);
        given(userRepository.findByUserName("user1")).willReturn(user2);

        assertThrows(UserAlreadyExistsException.class, () -> userServiceImpl.addUser(userDto1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_EmailAlreadyExists() {
        given(userMapper.toEntity(any(UserDto.class))).willReturn(user1);
        given(userRepository.findByUserName("user1")).willReturn(null);
        given(userRepository.findUserByEmail("user1@example.com")).willReturn(user2);

        assertThrows(UserAlreadyExistsException.class, () -> userServiceImpl.addUser(userDto1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_NameAlreadyExists() {
        given(userMapper.toEntity(any(UserDto.class))).willReturn(user1);
        given(userRepository.findByUserName("user1")).willReturn(null);
        given(userRepository.findUserByEmail("user1@example.com")).willReturn(null);
        given(userRepository.findUserByName("User One")).willReturn(user2);

        assertThrows(UserAlreadyExistsException.class, () -> userServiceImpl.addUser(userDto1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_RoleNotFound() {
        given(userMapper.toEntity(any(UserDto.class))).willReturn(user1);
        given(userRepository.findByUserName("user1")).willReturn(null);
        given(userRepository.findUserByEmail("user1@example.com")).willReturn(null);
        given(userRepository.findUserByName("User One")).willReturn(null);
        given(roleServiceImpl.findRoleByIdRole(2L)).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.addUser(userDto1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindAllUsers() {
        given(userRepository.findAll()).willReturn(List.of(user1, user2));
        given(userMapper.toDto(user1)).willReturn(userDto1);
        given(userMapper.toDto(user2)).willReturn(userDto2);

        ResponseEntity<List<UserDto>> response = userServiceImpl.findAllUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testFindUserByUserName() {
        given(userRepository.findByUserName("user1")).willReturn(user1);
        given(userMapper.toDto(user1)).willReturn(userDto1);

        ResponseEntity<UserDto> response = userServiceImpl.findUserByUserName("user1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user1", response.getBody().getUserName());
    }

    @Test
    void testFindUserByUserName_NotFound() {
        given(userRepository.findByUserName("non_existent")).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.findUserByUserName("non_existent"));
    }

    @Test
    void testFindUserByName() {
        given(userRepository.findUserByName("User One")).willReturn(user1);
        given(userMapper.toDto(user1)).willReturn(userDto1);

        ResponseEntity<UserDto> response = userServiceImpl.findUserByName("User One");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User One", response.getBody().getName());
    }

    @Test
    void testFindUserByName_NotFound() {
        given(userRepository.findUserByName("Inexistente")).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.findUserByName("Inexistente"));
    }

    @Test
    void testFindUserByEmail() {
        given(userRepository.findUserByEmail("user1@example.com")).willReturn(user1);
        given(userMapper.toDto(user1)).willReturn(userDto1);

        ResponseEntity<UserDto> response = userServiceImpl.findUserByEmail("user1@example.com");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user1@example.com", response.getBody().getEmail());
    }

    @Test
    void testFindUserByEmail_NotFound() {
        given(userRepository.findUserByEmail("inexistente@example.com")).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.findUserByEmail("inexistente@example.com"));
    }

    @Test
    void testFindUsersByRole() {
        given(roleServiceImpl.findRoleByRoleName("ROLE_USER")).willReturn(role1);
        given(userRepository.findUsersByRole(role1)).willReturn(List.of(user1, user2));
        given(userMapper.toDto(user1)).willReturn(userDto1);
        given(userMapper.toDto(user2)).willReturn(userDto2);

        ResponseEntity<List<UserDto>> response = userServiceImpl.findUsersByRole("ROLE_USER");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testFindAllReservesByUserId() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user1));

        ResponseEntity<List<Reserve>> response = userServiceImpl.findAllReservesByUserId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testFindAllReservesByUserId_NotFound() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.findAllReservesByUserId(999L));
    }

    @Test
    void testUpdateUser() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user1));
        given(bcrypt.encode(anyString())).willReturn("encryptedPassword");
        given(userMapper.toEntity(any(UserDto.class))).willReturn(user1);
        given(userRepository.save(any(User.class))).willReturn(user1);

        ResponseEntity<User> response = userServiceImpl.updateUser(1L, userDto1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.updateUser(999L, userDto1));
    }

    @Test
    void testDeleteUser() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user1));

        String result = userServiceImpl.deleteUser(1L);

        assertEquals("Usuario eliminado correctamente", result);
        verify(userRepository).delete(user1);
    }

    @Test
    void testDeleteUser_NotFound() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.deleteUser(999L));
    }

    @Test
    void testLoadUserByUsername() {
        given(userRepository.findByUserName("user1")).willReturn(user1);

        UserDetails userDetails = userServiceImpl.loadUserByUsername("user1");

        assertNotNull(userDetails);
        assertEquals("user1", userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        given(userRepository.findByUserName("non_existent")).willReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.loadUserByUsername("non_existent"));
    }
}

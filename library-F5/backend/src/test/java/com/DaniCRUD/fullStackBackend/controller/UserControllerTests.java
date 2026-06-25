package com.DaniCRUD.fullStackBackend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.DaniCRUD.fullStackBackend.dto.response.UserDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.exception.UserAlreadyExistsException;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setup() {
        user1 = User.builder().id(1L).userName("user1").build();
        userDto1 = UserDto.builder()
                .id(1L)
                .userName("user1")
                .name("User One")
                .email("user1@example.com")
                .password("password123")
                .build();
        userDto2 = UserDto.builder()
                .id(2L)
                .userName("user2")
                .name("User Two")
                .email("user2@example.com")
                .password("password456")
                .build();
    }

    @Test
    void testSaveUser() throws Exception {
        given(userService.addUser(any(UserDto.class)))
                .willReturn(new ResponseEntity<>(user1, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userName").value("user1"));
    }

    @Test
    void testSaveUser_ValidationError() throws Exception {
        UserDto invalidDto = UserDto.builder().userName(null).build();

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveUser_DuplicateUser() throws Exception {
        given(userService.addUser(any(UserDto.class)))
                .willThrow(new UserAlreadyExistsException("El usuario ya existe"));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllUsers() throws Exception {
        given(userService.findAllUsers())
                .willReturn(new ResponseEntity<>(List.of(userDto1, userDto2), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].userName").value("user1"))
                .andExpect(jsonPath("$[1].userName").value("user2"));
    }

    @Test
    void testFindByUserName() throws Exception {
        given(userService.findUserByUserName("user1"))
                .willReturn(new ResponseEntity<>(userDto1, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/users/1/user1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("user1"));
    }

    @Test
    void testFindByUserName_NotFound() throws Exception {
        given(userService.findUserByUserName("Inexistent"))
                .willThrow(new ResourceNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/api/v1/users/1/Inexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindByName() throws Exception {
        given(userService.findUserByName("User One"))
                .willReturn(new ResponseEntity<>(userDto1, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/users/2/User One")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User One"));
    }

    @Test
    void testFindByName_NotFound() throws Exception {
        given(userService.findUserByName("Inexistent"))
                .willThrow(new ResourceNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/api/v1/users/2/Inexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindByEmail() throws Exception {
        given(userService.findUserByEmail("user1@example.com"))
                .willReturn(new ResponseEntity<>(userDto1, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/users/3/user1@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    void testFindByEmail_NotFound() throws Exception {
        given(userService.findUserByEmail("inexistente@example.com"))
                .willThrow(new ResourceNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/api/v1/users/3/inexistente@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindUserByRole() throws Exception {
        given(userService.findUsersByRole("ROLE_USER"))
                .willReturn(new ResponseEntity<>(List.of(userDto1), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/users/4/ROLE_USER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testFindAllReservesByUserId() throws Exception {
        given(userService.findAllReservesByUserId(1L))
                .willReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/users/5/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUser() throws Exception {
        given(userService.updateUser(eq(1L), any(UserDto.class)))
                .willReturn(new ResponseEntity<>(user1, HttpStatus.OK));

        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        given(userService.updateUser(eq(999L), any(UserDto.class)))
                .willThrow(new ResourceNotFoundException("Usuario no encontrado"));

        mockMvc.perform(put("/api/v1/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        given(userService.deleteUser(1L))
                .willReturn("Usuario eliminado correctamente");

        mockMvc.perform(delete("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado correctamente"));
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        given(userService.deleteUser(999L))
                .willThrow(new ResourceNotFoundException("Usuario no encontrado"));

        mockMvc.perform(delete("/api/v1/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

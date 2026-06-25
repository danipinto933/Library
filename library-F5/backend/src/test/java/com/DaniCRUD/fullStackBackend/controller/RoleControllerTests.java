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

import com.DaniCRUD.fullStackBackend.dto.response.RoleDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = RoleController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
public class RoleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role role1;
    private RoleDto roleDto1;
    private RoleDto roleDto2;

    @BeforeEach
    void setup() {
        role1 = Role.builder().id(1L).role("ROLE_USER").build();
        roleDto1 = RoleDto.builder().id(1L).role("ROLE_USER").build();
        roleDto2 = RoleDto.builder().id(2L).role("ROLE_ADMIN").build();
    }

    @Test
    void testSaveRole() throws Exception {
        given(roleService.addRole(any(RoleDto.class)))
                .willReturn(new ResponseEntity<>(role1, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void testSaveRole_ValidationError() throws Exception {
        RoleDto invalidDto = RoleDto.builder().role(null).build();

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllRoles() throws Exception {
        given(roleService.findAllRoles())
                .willReturn(new ResponseEntity<>(List.of(roleDto1, roleDto2), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].role").value("ROLE_USER"))
                .andExpect(jsonPath("$[1].role").value("ROLE_ADMIN"));
    }

    @Test
    void testUpdateRole() throws Exception {
        Role updatedRole = Role.builder().id(1L).role("ROLE_MODERATOR").build();
        RoleDto updateDto = RoleDto.builder().role("ROLE_MODERATOR").build();

        given(roleService.updateRole(eq(1L), any(RoleDto.class)))
                .willReturn(new ResponseEntity<>(updatedRole, HttpStatus.OK));

        mockMvc.perform(put("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.role").value("ROLE_MODERATOR"));
    }

    @Test
    void testUpdateRole_NotFound() throws Exception {
        given(roleService.updateRole(eq(999L), any(RoleDto.class)))
                .willThrow(new ResourceNotFoundException("El rol no se ha encontrado"));

        mockMvc.perform(put("/api/v1/roles/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteRole() throws Exception {
        given(roleService.deleteRole(1L))
                .willReturn("Rol eliminado correctamente");

        mockMvc.perform(delete("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Rol eliminado correctamente"));
    }

    @Test
    void testDeleteRole_NotFound() throws Exception {
        given(roleService.deleteRole(999L))
                .willThrow(new ResourceNotFoundException("El rol no se ha encontrado"));

        mockMvc.perform(delete("/api/v1/roles/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

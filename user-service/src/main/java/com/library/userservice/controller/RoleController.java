package com.library.userservice.controller;

import com.library.userservice.dto.RoleDto;
import com.library.userservice.model.Role;
import com.library.userservice.service.RoleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    
    private final RoleService roleService;

    @PostMapping("")
    public ResponseEntity<Role> addRole(@Valid @RequestBody RoleDto roleDto) {
        return roleService.addRole(roleDto);
    }

    @GetMapping("")
    public ResponseEntity<List<RoleDto>> findAllRoles() {
        return roleService.findAllRoles();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRol(@PathVariable Long id, @Valid @RequestBody RoleDto rolDto) {
        return roleService.updateRole(id, rolDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        return new ResponseEntity<>(roleService.deleteRole(id), HttpStatus.OK);
    }
}

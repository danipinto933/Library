package com.DaniCRUD.fullStackBackend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.DaniCRUD.fullStackBackend.dto.response.RoleDto;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.service.RoleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/roles") 
public class RoleController
{
    private RoleService roleService;

    @PostMapping("")
    public ResponseEntity<Role> addRole(@Valid @RequestBody RoleDto roleDto)
    {
        return roleService.addRole(roleDto);
    }

    @GetMapping("")
    public ResponseEntity<List<RoleDto>> findAllRoles()
    {
        return roleService.findAllRoles();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRol(@PathVariable Long id, @Valid @RequestBody RoleDto rolDto)
    {
        return roleService.updateRole(id, rolDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id)
    {
        return new ResponseEntity<>(roleService.deleteRole(id), HttpStatus.OK);
    }
    
}

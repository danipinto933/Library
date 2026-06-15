package com.DaniCRUD.fullStackBackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.DaniCRUD.fullStackBackend.dto.response.UserDto;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/v1/users") //es buenas practicas poner api, seguido de la version, seguido de la tabla
public class UserController
{
    private UserService userService;

    public UserController (UserService userService)
    {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<User> addUser(@Valid @RequestBody UserDto userDto) //@RequestBody permite interpretar la informacion del cliente en el frontend, que esta deba transformarse en un JSON y lo podra leer
    {
        return userService.addUser(userDto);
    }

    @GetMapping("")
    public ResponseEntity<List<UserDto>> findAllUsers()
    {
        return userService.findAllUsers();
    }

    @GetMapping("/1/{userName}")
    public ResponseEntity<UserDto> findByUserName(@PathVariable String userName)
    {
        return userService.findUserByUserName(userName);
    }

    @GetMapping("/2/{name}")
    public ResponseEntity<UserDto> findByName(@PathVariable String name)
    {
        return userService.findUserByName(name);
    }

    @GetMapping("/3/{email}")
    public ResponseEntity<UserDto> findByEmail(@PathVariable String email)
    {
        return userService.findUserByEmail(email);
    }

    @GetMapping("/4/{roleName}")
    public ResponseEntity<List<UserDto>> findUserByRole(@PathVariable String roleName)
    {
        return userService.findUsersByRole(roleName);
    }

    @GetMapping("/5/{idUser}")
    public ResponseEntity<List<Reserve>> findAllReservesByUserId(@PathVariable Long idUser)
    {
        return userService.findAllReservesByUserId(idUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDto userDto)
    {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id)
    {
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }
}
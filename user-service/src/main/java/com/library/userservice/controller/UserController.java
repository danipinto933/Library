package com.library.userservice.controller;

import com.library.userservice.dto.UserDto;
import com.library.userservice.model.User;
import com.library.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<User> addUser(@Valid @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @GetMapping("")
    public ResponseEntity<List<UserDto>> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        User user = userService.findByIdUser(id);
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .userName(user.getUserName())
                .build();
        return org.springframework.http.ResponseEntity.ok(dto);
    }

    @GetMapping("/1/{userName}")
    public ResponseEntity<UserDto> findByUserName(@PathVariable String userName) {
        return userService.findUserByUserName(userName);
    }

    @GetMapping("/2/{name}")
    public ResponseEntity<UserDto> findByName(@PathVariable String name) {
        return userService.findUserByName(name);
    }

    @GetMapping("/3/{email}")
    public ResponseEntity<UserDto> findByEmail(@PathVariable String email) {
        return userService.findUserByEmail(email);
    }

    @GetMapping("/4/{roleName}")
    public ResponseEntity<List<UserDto>> findUserByRole(@PathVariable String roleName) {
        return userService.findUsersByRole(roleName);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }
}

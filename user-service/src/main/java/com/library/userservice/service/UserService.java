package com.library.userservice.service;

import com.library.userservice.dto.UserDto;
import com.library.userservice.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<User> addUser(UserDto userDto);
    ResponseEntity<List<UserDto>> findAllUsers();
    ResponseEntity<UserDto> findUserByUserName(String userName);
    ResponseEntity<UserDto> findUserByName(String name);
    ResponseEntity<UserDto> findUserByEmail(String email);
    ResponseEntity<List<UserDto>> findUsersByRole(String roleName);
    User findUserByNameUser(String userName);
    User findByIdUser(Long id);
    ResponseEntity<User> updateUser(Long id, UserDto userDto);
    String deleteUser(Long id);
}

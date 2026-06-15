package com.DaniCRUD.fullStackBackend.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.DaniCRUD.fullStackBackend.dto.response.UserDto;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.User;

@Service
public interface UserService //Defino que metodos voy a usar
{
    ResponseEntity<User> addUser (UserDto userDto); //devuelve objetos y un codigo HTTP y el frontend sepa que ocurre

    ResponseEntity<List<UserDto>> findAllUsers();
    ResponseEntity<UserDto> findUserByUserName (String userName);
    ResponseEntity<UserDto> findUserByName (String name);
    ResponseEntity<UserDto> findUserByEmail (String email);
    ResponseEntity<List<UserDto>> findUsersByRole (String roleName);
    ResponseEntity<List<Reserve>> findAllReservesByUserId (Long id);
    User findUserByNameUser(String name);
    User findByIdUser (Long id);

    ResponseEntity<User> updateUser(Long id, UserDto userDto);

    String deleteUser(Long id);
}
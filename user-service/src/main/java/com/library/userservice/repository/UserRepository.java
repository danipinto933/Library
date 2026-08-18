package com.library.userservice.repository;

import com.library.userservice.model.Role;
import com.library.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);

    User findByUserNameIgnoreCase(String userName);

    List<User> findByUserNameContainingIgnoreCase(String userName);

    User findUserByName(String name);

    User findUserByNameIgnoreCase(String name);

    List<User> findUserByNameContainingIgnoreCase(String name);

    User findUserByEmail(String email);

    User findUserByEmailIgnoreCase(String email);

    List<User> findUsersByRole(Role role);
}

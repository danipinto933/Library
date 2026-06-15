package com.DaniCRUD.fullStackBackend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    public User findByUserName (String userName);
    public User findUserByName (String name);
    public User findUserByEmail (String email);
    public List<User> findUsersByRole (Role role);
}

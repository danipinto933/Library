package com.DaniCRUD.fullStackBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.DaniCRUD.fullStackBackend.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
    public Role findByRole (String role);
}

package com.capstone.authServer.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.capstone.authServer.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // e.g. findByName
    Optional<Role> findByName(String name);
}

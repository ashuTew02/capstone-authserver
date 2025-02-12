package com.capstone.authServer.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.capstone.authServer.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1) Find by email
    Optional<User> findByEmail(String email);

    // 2) Find by oauthId
    Optional<User> findByOauthId(String oauthId);
    
    /*
      More custom queries if needed, e.g.:
      @Query("SELECT u FROM User u JOIN FETCH u.userTenantMappings WHERE u.id = :id")
      Optional<User> findUserWithTenantMappings(@Param("id") Long id);
    */
}

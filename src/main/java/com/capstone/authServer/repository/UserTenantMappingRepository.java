package com.capstone.authServer.repository;

import com.capstone.authServer.model.UserTenantMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTenantMappingRepository extends JpaRepository<UserTenantMapping, Long> {
    
    // e.g. find the single mapping for a user and a specific tenant
    Optional<UserTenantMapping> findByUserIdAndTenantId(Long userId, Long tenantId);
    
    // or find multiple if needed
    List<UserTenantMapping> findByUserId(Long userId);
}

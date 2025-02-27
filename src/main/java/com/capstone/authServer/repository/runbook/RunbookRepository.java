package com.capstone.authServer.repository.runbook;

import com.capstone.authServer.model.runbook.Runbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunbookRepository extends JpaRepository<Runbook, Long> {
    List<Runbook> findByTenantId(Long tenantId);
}

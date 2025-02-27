package com.capstone.authServer.repository.runbook;

import com.capstone.authServer.model.runbook.RunbookAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunbookActionRepository extends JpaRepository<RunbookAction, Long> {
}

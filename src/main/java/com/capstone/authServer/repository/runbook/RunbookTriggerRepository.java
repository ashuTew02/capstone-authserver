package com.capstone.authServer.repository.runbook;

import com.capstone.authServer.model.runbook.RunbookTrigger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunbookTriggerRepository extends JpaRepository<RunbookTrigger, Long> {
}

package com.capstone.authServer.repository.runbook;

import com.capstone.authServer.model.runbook.RunbookFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunbookFilterRepository extends JpaRepository<RunbookFilter, Long> {
}

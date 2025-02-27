package com.capstone.authServer.dto.runbook;

import com.capstone.authServer.model.FindingSeverity;
import com.capstone.authServer.model.FindingState;

public class ConfigureFiltersRequest {
    private Long runbookId;
    private FindingState state;      // e.g. "OPEN" (or null)
    private FindingSeverity severity;   // e.g. "HIGH" (or null)

    // Getters and setters
    public Long getRunbookId() { return runbookId; }
    public void setRunbookId(Long runbookId) { this.runbookId = runbookId; }

    public FindingState getState() { return state; }
    public void setState(FindingState state) { this.state = state; }

    public FindingSeverity getSeverity() { return severity; }
    public void setSeverity(FindingSeverity severity) { this.severity = severity; }
}

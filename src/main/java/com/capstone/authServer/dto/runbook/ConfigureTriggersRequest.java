package com.capstone.authServer.dto.runbook;

import java.util.List;

public class ConfigureTriggersRequest {
    private Long runbookId;
    private List<TriggerDTO> triggers;

    // Getters and setters

    public Long getRunbookId() { return runbookId; }
    public void setRunbookId(Long runbookId) { this.runbookId = runbookId; }

    public List<TriggerDTO> getTriggers() { return triggers; }
    public void setTriggers(List<TriggerDTO> triggers) { this.triggers = triggers; }
}

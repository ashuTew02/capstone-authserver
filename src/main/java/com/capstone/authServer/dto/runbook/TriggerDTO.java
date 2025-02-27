package com.capstone.authServer.dto.runbook;

import com.capstone.authServer.model.runbook.RunbookTriggerType;

public class TriggerDTO {
    private RunbookTriggerType triggerType;  // e.g. "NEW_SCAN_INITIATE"
    // private String config;       // JSON or text config

    // Getters and setters
    public RunbookTriggerType getTriggerType() { return triggerType; }
    public void setTriggerType(RunbookTriggerType triggerType) { this.triggerType = triggerType; }

    // public String getConfig() { return config; }
    // public void setConfig(String config) { this.config = config; }
}

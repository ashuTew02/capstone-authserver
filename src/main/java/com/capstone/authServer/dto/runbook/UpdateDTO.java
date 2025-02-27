package com.capstone.authServer.dto.runbook;

import com.capstone.authServer.model.FindingState;

public class UpdateDTO {
    // private String from;
    private FindingState to;

    // Getters and setters
    // public String getFrom() { return from; }
    // public void setFrom(String from) { this.from = from; }

    public FindingState getTo() { return to; }
    public void setTo(FindingState to) { this.to = to; }
}

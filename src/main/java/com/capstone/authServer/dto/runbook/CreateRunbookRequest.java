package com.capstone.authServer.dto.runbook;

public class CreateRunbookRequest {
    private String name;
    private String description;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

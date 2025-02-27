package com.capstone.authServer.dto.runbook;

public class ConfigureActionsRequest {
    private Long runbookId;
    private UpdateDTO update;          // now only "to"
    private Boolean ticketCreate;      // still the same

    public Long getRunbookId() {
        return runbookId;
    }
    public void setRunbookId(Long runbookId) {
        this.runbookId = runbookId;
    }

    public UpdateDTO getUpdate() {
        return update;
    }
    public void setUpdate(UpdateDTO update) {
        this.update = update;
    }

    public Boolean getTicketCreate() {
        return ticketCreate;
    }
    public void setTicketCreate(Boolean ticketCreate) {
        this.ticketCreate = ticketCreate;
    }
}

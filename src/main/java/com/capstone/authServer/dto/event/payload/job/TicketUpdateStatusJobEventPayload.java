package com.capstone.authServer.dto.event.payload.job;

import com.capstone.authServer.dto.event.payload.ticket.TicketUpdateStatusEventPayload;
import com.capstone.authServer.model.KafkaTopic;
import com.capstone.authServer.model.Tool;

public class TicketUpdateStatusJobEventPayload {
    String findingId;
    Long tenantId;
    Long jobId;
    Tool tool;
    String status;

    public TicketUpdateStatusJobEventPayload(String findingId, Long tenantId, KafkaTopic destTopic, Tool tool,
            String status, Long jobId) {
        this.findingId = findingId;
        this.tenantId = tenantId;
        this.tool = tool;
        this.status = status;
        this.jobId = jobId;
    }

    public TicketUpdateStatusJobEventPayload(String findingId, Long tenantId, KafkaTopic destTopic, Tool tool, Long jobId) {
        this.findingId = findingId;
        this.tenantId = tenantId;
        this.tool = tool;
        this.status = "DONE";
        this.jobId = jobId;
    }

    public TicketUpdateStatusJobEventPayload(TicketUpdateStatusEventPayload payload,  Long jobId) {
        this.findingId = payload.getFindingId();
        this.tenantId = payload.getTenantId();
        this.tool = payload.getTool();
        this.status = payload.getStatus();
        this.status = "DONE";
        this.jobId = jobId;
    }

    public TicketUpdateStatusJobEventPayload() {
    }

    public String getFindingId() {
        return findingId;
    }
    public void setFindingId(String findingId) {
        this.findingId = findingId;
    }
    public Long getTenantId() {
        return tenantId;
    }
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Tool getTool() {
        return tool;
    }
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
}

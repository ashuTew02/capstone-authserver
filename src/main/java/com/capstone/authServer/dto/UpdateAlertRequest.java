package com.capstone.authServer.dto;

import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.Tool;

public class UpdateAlertRequest {

    private Tool tool;
    private Long alertNumber;
    private FindingState findingState;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public Long getAlertNumber() {
        return alertNumber;
    }

    public void setAlertNumber(Long alertNumber) {
        this.alertNumber = alertNumber;
    }

    public FindingState getFindingState() {
        return findingState;
    }

    public void setFindingState(FindingState findingState) {
        this.findingState = findingState;
    }
}

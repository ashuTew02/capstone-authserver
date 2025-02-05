package com.capstone.authServer.dto;

import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;

public class UpdateAlertRequest {

    private ScanToolType tool;
    private Long alertNumber;
    private FindingState findingState;

    public ScanToolType getTool() {
        return tool;
    }

    public void setTool(ScanToolType tool) {
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

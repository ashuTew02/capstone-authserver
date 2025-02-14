package com.capstone.authServer.dto.event.payload;

import com.capstone.authServer.model.Tool;

public class ScanParseJobEventPayload {
    private Tool tool;
    private Long tenantId;
    private String scanFilePath;

    public ScanParseJobEventPayload(Tool tool, Long tenantId, String scanFilePath) {
        this.tool = tool;
        this.tenantId = tenantId;
        this.scanFilePath = scanFilePath;
    }

    public ScanParseJobEventPayload() {

    }

    public Tool getTool() {
        return tool;
    }
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    public String getScanFilePath() {
        return scanFilePath;
    }
    public void setScanFilePath(String scanFilePath) {
        this.scanFilePath = scanFilePath;
    }
    public Long getTenantId() {
        return tenantId;
    }
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    
}

package com.capstone.authServer.dto.scan.request;

import com.capstone.authServer.model.Tool;
import java.util.List;

public class ScanRequestDTO {

    private List<Tool> toolsToScan;
    Boolean scanAll = false;

    public Boolean getScanAll() {
        return scanAll;
    }

    public void setScanAll(Boolean scanAll) {
        this.scanAll = scanAll;
    }

    public ScanRequestDTO() {
    }

    public ScanRequestDTO(List<Tool> toolsToScan) {
        this.toolsToScan = toolsToScan;
    }

    public List<Tool> getToolsToScan() {
        return toolsToScan;
    }

    public void setToolsToScan(List<Tool> toolsToScan) {
        this.toolsToScan = toolsToScan;
    }
}

package com.capstone.authServer.dto.scan.request;

import com.capstone.authServer.model.ScanType;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class ScanRequestDTO {

    @NotEmpty(message = "scanTypes list must not be empty")
    private List<ScanType> scanTypes;

    public ScanRequestDTO() {
    }

    public ScanRequestDTO(List<ScanType> scanTypes) {
        this.scanTypes = scanTypes;
    }

    public List<ScanType> getScanTypes() {
        return scanTypes;
    }

    public void setScanTypes(List<ScanType> scanTypes) {
        this.scanTypes = scanTypes;
    }
}

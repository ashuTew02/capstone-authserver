package com.capstone.authServer.dto.event;

import java.util.List;

import com.capstone.authServer.model.ScanType;

public class ScanRequestEvent {

    private String owner;
    private String repository;
    private List<ScanType> scanTypes;

    public ScanRequestEvent() {
    }

    public ScanRequestEvent(String owner, String repository, List<ScanType> scanTypes) {
        this.owner = owner;
        this.repository = repository;
        this.scanTypes = scanTypes;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public List<ScanType> getScanTypes() {
        return scanTypes;
    }

    public void setScanTypes(List<ScanType> scanTypes) {
        this.scanTypes = scanTypes;
    }
}

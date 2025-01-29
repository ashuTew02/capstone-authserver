package com.capstone.authServer.model;

public enum ScanType {
    ALL("all"),
    CODE_SCAN("code-scan"),
    DEPENDABOT("dependabot"),
    SECRET_SCAN("secret-scan");

    private final String value;

    ScanType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

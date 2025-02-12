package com.capstone.authServer.dto.response;

public class CurrentTenantDTO {
    private Long tenantId;
    private String tenantName;
    private String roleName;

    // Constructors
    public CurrentTenantDTO() {
    }

    public CurrentTenantDTO(Long tenantId, String tenantName, String roleName) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.roleName = roleName;
    }

    // Getters and setters
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

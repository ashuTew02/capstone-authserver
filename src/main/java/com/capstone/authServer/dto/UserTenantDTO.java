package com.capstone.authServer.dto;

public class UserTenantDTO {
    private Long tenantId;
    private String tenantName;
    private String roleName;

    public UserTenantDTO(Long tenantId, String tenantName, String roleName) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.roleName = roleName;
    }

    // Getters/Setters
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

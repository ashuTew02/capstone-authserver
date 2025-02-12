package com.capstone.authServer.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oauthId;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String name;
    private String imageUrl;
    private String provider;
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @Column(nullable = false, updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp createdAt;

    @Column(nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private java.sql.Timestamp updatedAt;

    @Column(name = "default_tenant_id", nullable = false)
    private Long defaultTenantId;
    
    // The defaultTenant relationship:
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_tenant_id", insertable = false, updatable = false)
    @JsonIgnore
    private Tenant defaultTenant;
    
    // A user can be in multiple tenants
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"tenant", "user"}) 
    private List<UserTenantMapping> userTenantMappings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public java.sql.Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDefaultTenantId() {
        return defaultTenantId;
    }

    public void setDefaultTenantId(Long defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    public Tenant getDefaultTenant() {
        return defaultTenant;
    }

    public void setDefaultTenant(Tenant defaultTenant) {
        this.defaultTenant = defaultTenant;
    }

    public List<UserTenantMapping> getUserTenantMappings() {
        return userTenantMappings;
    }

    public void setUserTenantMappings(List<UserTenantMapping> userTenantMappings) {
        this.userTenantMappings = userTenantMappings;
    }

    // Getters and Setters (omitted for brevity)
    
    /* 
       Example convenience method 
       (e.g. to quickly see which roles across which tenants the user has, etc.) 
    */
}

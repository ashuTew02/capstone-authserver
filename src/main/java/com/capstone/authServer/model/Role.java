package com.capstone.authServer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

// import org.hibernate.annotations.OnDelete;
// import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // If you want to see all user-tenant-mappings that have this role:
    @OneToMany(mappedBy = "role")
    // @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore()
    private java.util.List<UserTenantMapping> userTenantMappings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.List<UserTenantMapping> getUserTenantMappings() {
        return userTenantMappings;
    }

    public void setUserTenantMappings(java.util.List<UserTenantMapping> userTenantMappings) {
        this.userTenantMappings = userTenantMappings;
    }
    
    // Constructors, Getters, Setters
}

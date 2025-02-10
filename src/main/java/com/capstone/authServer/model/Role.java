package com.capstone.authServer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // e.g. "USER", "ADMIN", "SUPER_ADMIN"

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    // ---- Getters / Setters ---
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

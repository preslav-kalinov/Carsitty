package com.kalinov.carsitty.dto;

import com.kalinov.carsitty.RoleEnum;

public class AuthenticatedUserDto {
    private String username;
    private String displayName;
    private RoleEnum role;

    public AuthenticatedUserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }
}
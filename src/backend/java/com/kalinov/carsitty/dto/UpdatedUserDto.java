package com.kalinov.carsitty.dto;

import com.kalinov.carsitty.RoleEnum;

import javax.validation.constraints.*;

public class UpdatedUserDto {
    @Size(min = 3, max = 128, message = "The username length has to be between 3 and 128 characters")
    @Pattern(regexp = "^\\w+$", message = "The username can contain only numbers, letters or underscores")
    private String username;

    @Size(min = 3, max = 1024, message = "The display name length has to be between 3 and 1024 characters")
    private String displayName;

    @Size(min = 6, max = 60, message = "The password length has to be between 6 and 60 characters")
    @Pattern(regexp = "^[^\\s]+$", message = "The password cannot contain whitespaces")
    private String password;

    @Email(regexp = ".+[@].+[\\.].+", message = "The email is not valid")
    @Size(max = 1024, message = "The email length has to be less than 1024 characters")
    private String email;

    private RoleEnum role;

    private Boolean enabled;

    public UpdatedUserDto() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
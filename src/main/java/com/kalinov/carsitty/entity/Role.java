package com.kalinov.carsitty.entity;

import com.kalinov.carsitty.RoleEnum;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "roles")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role", columnDefinition = "enum('ROLE')")
    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum;

    public Role() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleEnum getRole() {
        return roleEnum;
    }

    public void setRole(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }
}
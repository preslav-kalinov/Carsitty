package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao extends JpaRepository<Role, Long> {
}
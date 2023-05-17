package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {
}
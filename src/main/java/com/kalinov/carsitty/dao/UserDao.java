package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDao extends JpaRepository<User, Long> {
    @Query("FROM User u WHERE u.username = ?1")
    List<User> getUsersByUsername(String username);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.username = ?1")
    Long getUserCountByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.email = ?1")
    Long getUserCountByEmail(String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.username = ?1")
    void deleteUserByUsername(String username);
}
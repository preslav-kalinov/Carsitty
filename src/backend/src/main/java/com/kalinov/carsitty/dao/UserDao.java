package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.RoleEnum;
import com.kalinov.carsitty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDao extends JpaRepository<User, Long> {
    @Query("FROM User u WHERE u.username = ?1")
    List<User> getUsersByUsername(String username);

    @Query("FROM User u WHERE u.username = ?1")
    User getUserByUsername(String username);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.username = ?1")
    Long getUserCountByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.email = ?1")
    Long getUserCountByEmail(String email);

    @Query("FROM User u JOIN Role r ON u.role = r.id WHERE r.role = ?1")
    List<User> getUsersByRole(RoleEnum role);

    @Modifying
    @Query("DELETE FROM User u WHERE u.username = ?1")
    void deleteUserByUsername(String username);
}
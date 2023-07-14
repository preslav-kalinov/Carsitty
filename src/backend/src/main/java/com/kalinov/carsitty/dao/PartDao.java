package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.Part;
import com.kalinov.carsitty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartDao extends JpaRepository<Part, Long> {
    @Query("SELECT p FROM Part p JOIN p.user u WHERE u.id = ?1") //"SELECT p FROM Part p JOIN p.user u WHERE u.id = :userId"
    List<Part> getPartsByUserId(Long userId);
}
package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.Sale;
import com.kalinov.carsitty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleDao extends JpaRepository<Sale, Long> {
    @Query("SELECT s FROM Sale s JOIN s.user u WHERE u.id = ?1")  //FROM User u JOIN Role r ON u.role = r.id WHERE r.role = ?1
    List<Sale> getSalesByUserId(Long userId);
}
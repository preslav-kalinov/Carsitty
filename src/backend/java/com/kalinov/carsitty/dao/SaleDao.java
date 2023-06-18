package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleDao extends JpaRepository<Sale, Long> {
}
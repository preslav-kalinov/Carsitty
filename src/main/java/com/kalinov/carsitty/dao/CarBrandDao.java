package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarBrandDao extends JpaRepository<CarBrand, Long> {
}
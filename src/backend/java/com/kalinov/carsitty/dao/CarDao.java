package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarDao extends JpaRepository<Car, Long> {
}
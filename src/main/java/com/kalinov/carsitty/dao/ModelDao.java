package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelDao extends JpaRepository<Model, Long> {
}
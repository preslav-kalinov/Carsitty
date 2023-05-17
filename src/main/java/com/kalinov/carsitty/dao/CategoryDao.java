package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDao extends JpaRepository<Category, Long> {
}
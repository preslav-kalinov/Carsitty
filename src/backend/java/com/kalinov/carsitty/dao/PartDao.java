package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartDao extends JpaRepository<Part, Long> {
}
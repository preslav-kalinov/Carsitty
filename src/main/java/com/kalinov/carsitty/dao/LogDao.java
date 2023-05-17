package com.kalinov.carsitty.dao;

import com.kalinov.carsitty.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogDao extends JpaRepository<Log, Long> {
}
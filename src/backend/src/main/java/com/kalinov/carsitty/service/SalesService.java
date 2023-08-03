package com.kalinov.carsitty.service;

import com.kalinov.carsitty.dao.SaleDao;
import com.kalinov.carsitty.entity.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SalesService {
    private final SaleDao saleDao;

    @Autowired
    public SalesService(SaleDao saleDao) {
        this.saleDao = saleDao;
    }

    public List<Sale> getAllSales()  {
        if (this.saleDao.count() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There are no available sales");
        }

        return this.saleDao.findAll();
    }
}
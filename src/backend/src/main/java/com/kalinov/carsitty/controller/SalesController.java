package com.kalinov.carsitty.controller;

import com.kalinov.carsitty.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/sales")
public class SalesController {
    private final SalesService salesService;

    @Autowired
    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getSales() throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(this.salesService.getAllSales());
    }
}
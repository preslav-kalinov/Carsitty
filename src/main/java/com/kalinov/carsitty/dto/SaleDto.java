package com.kalinov.carsitty.dto;

public class SaleDto {
    private Long soldQuantity;

    public SaleDto(){
    }

    public Long getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Long soldQuantity) {
        this.soldQuantity = soldQuantity;
    }
}
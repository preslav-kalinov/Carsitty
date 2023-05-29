package com.kalinov.carsitty.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SaleDto {
    @NotNull(message = "The part sold quantity cannot be empty")
    @Min(value = 0, message = "The part sold quantity must be a positive number")
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
package com.kalinov.carsitty.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class NewSaleDto {
    @NotNull(message = "The part sold quantity cannot be empty")
    @Min(value = 0, message = "The part sold quantity must be a positive number")
    private Long soldQuantity;

    public NewSaleDto() {
    }

    public Long getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Long soldQuantity) {
        this.soldQuantity = soldQuantity;
    }
}
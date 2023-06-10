package com.kalinov.carsitty.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class DiscountDto {
    @NotNull(message = "The part discount cannot be empty")
    @Min(value = 0, message = "The part discount must be a positive number")
    private Long partDiscount;

    public DiscountDto(){
    }

    public Long getPartDiscount() {
        return partDiscount;
    }

    public void setPartDiscount(Long partDiscount) {
        this.partDiscount = partDiscount;
    }
}
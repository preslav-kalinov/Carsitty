package com.kalinov.carsitty.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class NewPartDto {
    @NotBlank(message = "The part name cannot be empty")
    @Size(min = 3, max = 1024, message = "The part name must be between 3 and 1024 characters long")
    private String name;

    @NotNull(message = "The part quantity cannot be empty")
    @Min(value = 0, message = "The part quantity must be a positive number")
    private Long quantity;

    @NotNull(message = "The part price cannot be empty")
    @Min(value = 0, message = "The part price must be a positive number")
    private BigDecimal price;

    @NotNull(message = "The part category cannot be empty")
    private Long categoryId;

    @NotNull(message = "The part car cannot be empty")
    private Long carId;

    public NewPartDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
}
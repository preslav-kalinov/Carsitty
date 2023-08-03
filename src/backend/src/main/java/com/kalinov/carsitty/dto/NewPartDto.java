package com.kalinov.carsitty.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class NewPartDto {
    private String pictureUrl;

    @NotBlank(message = "The part name cannot be empty")
    @Size(min = 3, max = 1024, message = "The part name must be between 3 and 1024 characters long")
    private String name;

    @NotBlank(message = "The part oem cannot be empty")
    @Size(min = 3, max = 128, message = "The part oem must be between 3 and 128 characters long")
    private String oem;

    @NotNull(message = "The part quantity cannot be empty")
    @Min(value = 0, message = "The part quantity must be a positive number")
    private Long quantity;

    @NotNull(message = "The part price cannot be empty")
    @Min(value = 0, message = "The part price must be a positive number")
    private BigDecimal price;

    @NotNull(message = "The part category cannot be empty")
    private Long categoryId;

    @NotNull(message = "The part cars cannot be empty")
    private Set<Long> carIds;

    public NewPartDto() {
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOem() {
        return oem;
    }

    public void setOem(String oem) {
        this.oem = oem;
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

    public Set<Long> getCarIds() {
        return carIds;
    }

    public void setCarIds(Set<Long> carIds) {
        this.carIds = carIds;
    }
}
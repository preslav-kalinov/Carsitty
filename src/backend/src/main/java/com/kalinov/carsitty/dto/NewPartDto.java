package com.kalinov.carsitty.dto;

import javax.validation.constraints.*;
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

    @NotNull(message = "The part discount cannot be empty, but can be 0")
    @Min(value = 0, message = "The part discount must be a positive number")
    @Max(value = 95, message = "The part discount cannot exceed 95 percent")
    private Long discount;

    @NotNull(message = "The part category cannot be empty")
    private Long categoryId;

    @NotNull(message = "The part cars cannot be empty")
    @NotEmpty(message = "The part cars cannot be empty")
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

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
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
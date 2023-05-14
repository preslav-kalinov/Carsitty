package com.kalinov.carsitty.dto;

import com.kalinov.carsitty.RoleEnum;

public class NewManagerDto extends UserDto {
    public NewManagerDto() {
        super();
        this.setRole(RoleEnum.Manager);
    }
}
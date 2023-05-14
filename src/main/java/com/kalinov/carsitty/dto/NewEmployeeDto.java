package com.kalinov.carsitty.dto;

import com.kalinov.carsitty.RoleEnum;

public class NewEmployeeDto extends UserDto {
    public NewEmployeeDto() {
        super();
        this.setRole(RoleEnum.Employee);
    }
}
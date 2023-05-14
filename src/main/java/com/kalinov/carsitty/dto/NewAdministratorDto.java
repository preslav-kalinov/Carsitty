package com.kalinov.carsitty.dto;

import com.kalinov.carsitty.RoleEnum;

public class NewAdministratorDto extends UserDto {
    public NewAdministratorDto() {
        super();
        this.setRole(RoleEnum.Administrator);
    }
}
package com.kalinov.carsitty.dto;

import java.util.HashMap;

public class ExceptionDto {
    private String error;
    private HashMap<String, String> errorMessage;

    private ExceptionDto(){
    }

    public ExceptionDto(String error, HashMap<String, String> errorMessage){
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public HashMap<String, String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(HashMap<String, String> errorMessage) {
        this.errorMessage = errorMessage;
    }
}
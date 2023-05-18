package com.kalinov.carsitty.exception;

import java.io.Serializable;

public class CarsittyException extends Exception implements Serializable {
    private int statusCode;

    private String message;

    public CarsittyException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
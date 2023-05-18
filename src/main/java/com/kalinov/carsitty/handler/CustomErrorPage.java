package com.kalinov.carsitty.handler;

import com.kalinov.carsitty.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

public class CustomErrorPage {
    public static ResponseEntity createErrorPage(int errorCode, String errorMessage){
        ExceptionDto model = new ExceptionDto(errorCode, errorMessage);
        return ResponseEntity.status(errorCode).body(model);
    }

    public static ResponseEntity createBadRequestErrorPage() {
        int statusCode = HttpServletResponse.SC_BAD_REQUEST;
        return CustomErrorPage.createErrorPage(HttpServletResponse.SC_BAD_REQUEST, HttpStatus.resolve(statusCode).getReasonPhrase());
    }
}
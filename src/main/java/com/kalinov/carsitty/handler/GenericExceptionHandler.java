package com.kalinov.carsitty.handler;

import com.kalinov.carsitty.dto.ExceptionDto;
import com.kalinov.carsitty.exception.CarsittyException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(CarsittyException.class)
    public ResponseEntity handleCarsittyException(CarsittyException e){
        return CustomErrorPage.createErrorPage(e.getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return CustomErrorPage.createBadRequestErrorPage();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDto> handleDataIntegrityViolationException(HttpMessageNotReadableException e){
        return CustomErrorPage.createBadRequestErrorPage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return CustomErrorPage.createErrorPage(HttpServletResponse.SC_BAD_REQUEST, "One or more fields contain invalid values");
    }

    @ExceptionHandler(NoSuchMethodException.class)
    public ResponseEntity<ExceptionDto> handleNoSuchMethodException(NoSuchMethodException e) {
        return CustomErrorPage.createErrorPage(HttpServletResponse.SC_BAD_REQUEST, "An unexpected error has occurred while processing your request");
    }
}
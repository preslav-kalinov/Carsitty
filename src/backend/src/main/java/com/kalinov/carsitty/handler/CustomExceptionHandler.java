package com.kalinov.carsitty.handler;

import com.kalinov.carsitty.dto.ExceptionDto;
import com.kalinov.carsitty.service.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class CustomExceptionHandler {
    private final ExceptionService exceptionService;

    @Autowired
    public CustomExceptionHandler(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionDto> handleResponseStatusException(ResponseStatusException e) {
        return exceptionService.generateExceptionResponseEntity(e);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDto> handleDataIntegrityViolationException(HttpMessageNotReadableException e){
        return exceptionService.generateExceptionResponseEntity(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return exceptionService.generateExceptionResponseEntity(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return exceptionService.generateExceptionResponseEntityWithValidationErrors(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more fields contain invalid values"),
                e.getBindingResult().getAllErrors());
    }

    @ExceptionHandler(NoSuchMethodException.class)
    public ResponseEntity<ExceptionDto> handleNoSuchMethodException(NoSuchMethodException e) {
        return exceptionService.generateExceptionResponseEntity(
                new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error has occurred while processing your request"));
    }
}
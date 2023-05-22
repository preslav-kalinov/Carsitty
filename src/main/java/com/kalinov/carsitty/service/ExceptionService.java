package com.kalinov.carsitty.service;

import com.kalinov.carsitty.dto.ExceptionDto;
import com.kalinov.carsitty.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExceptionService {
    private final ModelMapperUtil modelMapper;

    @Autowired
    public ExceptionService(ModelMapperUtil modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<ExceptionDto> generateExceptionResponseEntity(ResponseStatusException e) {
        ExceptionDto responseBody = this.modelMapper.map(e, ExceptionDto.class);
        return ResponseEntity.status(e.getStatus()).body(responseBody);
    }

    public ResponseEntity<ExceptionDto> generateExceptionResponseEntityWithValidationErrors(ResponseStatusException e, List<ObjectError> validationErrors) {
        ExceptionDto responseBody = this.modelMapper.map(e, ExceptionDto.class);
        validationErrors.forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            responseBody.getErrorMessage().put(fieldName, message);
        });
        return ResponseEntity.status(e.getStatus()).body(responseBody);
    }
}
package com.kalinov.carsitty.config;

import com.kalinov.carsitty.dto.ExceptionDto;
import com.kalinov.carsitty.util.ModelMapperUtil;
import org.modelmapper.Converter;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@Configuration
public class ModelMapperCustomConfig {
    private final ModelMapperUtil modelMapper;

    @Autowired
    public ModelMapperCustomConfig(ModelMapperUtil modelMapper) {
        this.modelMapper = modelMapper;
        this.activateModelMapperConfigMethods();
    }

    private void activateModelMapperConfigMethods() {
        Method[] methods = ModelMapperCustomConfig.class.getDeclaredMethods();
        for(Method method : methods) {
            if(method.getName().startsWith("map")) {
                try {
                    method.invoke(this);
                } catch (IllegalAccessException | InvocationTargetException ignored) { }
            }
        }
    }

    private void mapResponseStatusExceptionToExceptionDto() {
        TypeMap<ResponseStatusException, ExceptionDto> propertyMapper = this.modelMapper.createTypeMap(ResponseStatusException.class, ExceptionDto.class);
        Converter<HttpStatus, String> responseStatusToString = c -> {
            if (c.getSource() == null)
                return null;

            return c.getSource().getReasonPhrase();
            };

        Converter<String, HashMap<String, String>> reasonStringToHashMap = c -> {
            HashMap<String, String> exceptionContent = new HashMap<>();
            exceptionContent.put("problem", c.getSource());
            return exceptionContent;
        };
        propertyMapper.addMappings(
                mapper -> mapper.using(responseStatusToString).map(ResponseStatusException::getStatus, ExceptionDto::setError)
        );
        propertyMapper.addMappings(
                mapper -> mapper.using(reasonStringToHashMap).map(ResponseStatusException::getReason, ExceptionDto::setErrorMessage)
        );
    }
}
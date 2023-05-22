package com.kalinov.carsitty.util;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ModelMapperUtil extends ModelMapper {
    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream().map(element
                -> this.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
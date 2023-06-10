package com.kalinov.carsitty.service;

import com.kalinov.carsitty.dao.LogDao;
import com.kalinov.carsitty.dto.AuthenticatedUserDto;
import com.kalinov.carsitty.dto.LogDto;
import com.kalinov.carsitty.entity.Log;
import com.kalinov.carsitty.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class LogService {
    private final LogDao logDao;
    private final ModelMapperUtil modelMapper;
    private final UserService userService;

    @Autowired
    public LogService(LogDao logDao, ModelMapperUtil modelMapper, UserService userService) {
        this.logDao = logDao;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    public List<LogDto> getLogs() {
        if (this.logDao.count() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Part logs not found");
        }
        List<LogDto> logDtoList = this.modelMapper.mapList(this.logDao.findAll(), LogDto.class);
        return logDtoList;
    }

    public void logCreatedPart(String partName) {
        String partCreatedMessage = String.format("created part '%s'", partName);
        this.insertLog(partCreatedMessage);
    }

    public void logUpdatedPart(Long partId, String partName) {
        String partUpdatedMessage = String.format("updated part '%s' with ID: %d", partName, partId);
        this.insertLog(partUpdatedMessage);
    }

    public void logDeletedPart(Long partId, String partName) {
        String partDeletedMessage = String.format("deleted part '%s' with ID: %d", partName, partId);
        this.insertLog(partDeletedMessage);
    }

    public void logSoldPart(Long partId, String partName) {
        String partDeletedMessage = String.format("sold part '%s' with ID: %d", partName, partId);
        this.insertLog(partDeletedMessage);
    }

    private void insertLog(String message) {
        Log log = new Log();
        String logMessage = this.getAuthenticatedUserLogMessage(message);
        log.setErrorMessage(logMessage);
        log.setIncidentTime(Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
        this.logDao.save(log);
    }

    private String getAuthenticatedUserLogMessage(String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUserDto authenticatedUserDto = this.userService.getAuthenticatedUser(authentication);
        return String.format("%s '%s' %s", authenticatedUserDto.getRole().toString(),
                authenticatedUserDto.getUsername(), message);
    }
}
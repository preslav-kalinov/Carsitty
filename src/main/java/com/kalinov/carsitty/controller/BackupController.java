package com.kalinov.carsitty.controller;

import com.kalinov.carsitty.service.DatabaseBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/backup")
public class BackupController {
    private final DatabaseBackupService backupService;

    @Autowired
    public BackupController(DatabaseBackupService backupService) {
        this.backupService = backupService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity backupDatabase() throws IOException {
        this.backupService.backupDatabase();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
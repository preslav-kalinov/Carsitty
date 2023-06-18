package com.kalinov.carsitty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DatabaseBackupService {
    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${carsitty.backup.filepath}")
    private String backupFolderPath;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${carsitty.backup.mysqlpath}")
    private String databasePath;

    private final FileService fileService;

    @Autowired
    public DatabaseBackupService(FileService fileService) {
        this.fileService = fileService;
    }

    public void backupDatabase() {
        String databaseName = this.databaseUrl.substring(this.databaseUrl.lastIndexOf("/") + 1);

        try {
            String backupFileName = generateBackupFileName(databaseName);
            String command = buildBackupCommand(databaseName, backupFileName);

            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String errorOutput="";

            String line;
            while ((line = reader.readLine()) != null) {
                errorOutput+=line;
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                this.fileService.writeToFile(String.format("Error creating database backup: " + errorOutput), true);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating database backup. Exit code: " + exitCode);
            }
            else {
                this.fileService.writeToFile(String.format("Database backup was created successfully: " + backupFileName));
            }
        } catch (IOException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occurred during database backup: " + e.getMessage());
        }
    }

    private String generateBackupFileName(String databaseName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        return String.format("%s_%s.sql", databaseName, timestamp);
    }

    private String buildBackupCommand(String databaseName, String backupFileName) {
        return String.format("cmd.exe /c %s/mysqldump -u %s -p%s %s > %s",
                databasePath, databaseUsername, databasePassword, databaseName,
                backupFolderPath + File.separator + backupFileName);
    }
}
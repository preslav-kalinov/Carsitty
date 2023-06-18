package com.kalinov.carsitty.service;

import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileService {
    private final String filePath;

    public FileService(String filePath) {
        this.filePath = filePath;
    }

    public void writeToFile(String log) throws IOException {
        writeToFile(log, false);
    }

    public void writeToFile(String log, boolean isError) throws IOException {
        Logger logger = Logger.getLogger("CarsittyLogs");
        FileHandler fileHandler;
        fileHandler = new FileHandler(filePath, true);
        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);

        if (isError) {
            logger.severe(log);
        } else {
            logger.info(log);
        }

        fileHandler.close();
    }
}
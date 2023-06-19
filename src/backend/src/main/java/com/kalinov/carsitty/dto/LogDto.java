package com.kalinov.carsitty.dto;

public class LogDto {
    private String incidentTime;
    private String message;

    public LogDto() {
    }

    public String getIncidentTime() {
        return incidentTime;
    }

    public void setIncidentTime(String incidentTime) {
        this.incidentTime = incidentTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
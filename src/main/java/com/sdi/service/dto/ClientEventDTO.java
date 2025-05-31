package com.sdi.service.dto;

import com.sdi.domain.ClientEventType;

import java.io.Serializable;
import java.time.LocalDate;

public class ClientEventDTO implements Serializable {

    private String event;
    private LocalDate eventDate;
    private String description;

    // No-arg constructor
    public ClientEventDTO() {
    }

    // All-arg constructor
    public ClientEventDTO(String event, LocalDate eventDate, String description) {
        this.event = event;
        this.eventDate = eventDate;
        this.description = description;

    }

    // Getters and Setters
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

}

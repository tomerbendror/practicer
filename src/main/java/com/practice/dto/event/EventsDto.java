package com.practice.dto.event;

import com.practice.model.Event;

import java.util.List;

/**
 * Created by liorm on 18/10/2015.
 */
public class EventsDto {

    private List<Event> events;

    public EventsDto(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public EventsDto() {
    }
}

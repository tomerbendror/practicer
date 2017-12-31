package com.practice.controller;

import com.practice.dto.event.EventsDto;
import com.practice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by liorm on 18/10/2015.
 */
@Controller
@RequestMapping("/event")
public class EventController extends BaseController {

    @Autowired
    private EventRepository eventRepository;

    @RequestMapping(value="/emit", method = RequestMethod.PUT)
//    @PreAuthorize("hasRole('child')")
    public void emitEvents(@RequestBody EventsDto events){
        eventRepository.emitEvent(events.getEvents());
    }
}

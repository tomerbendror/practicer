package com.practice.repository;

import com.practice.model.Event;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liorm on 18/10/2015.
 */
@Repository("eventRepository")
//@Transactional  currently i dont think we need it to be Transactional
public class EventRepository extends BaseRepository{

    public void emitEvent(List<Event> events){
        if(!CollectionUtils.isEmpty(events)) {
            for (Event event : events) {
                getEm().persist(event);
            }
        }
    }
}

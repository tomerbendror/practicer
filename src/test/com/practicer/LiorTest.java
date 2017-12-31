package com.practicer;


import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.practice.dto.event.EventsDto;
import com.practice.model.Event;
import com.practice.model.EventType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;

/**
 * Created by liorm on 20/10/2015.
 */

public class LiorTest {

    public static void main(String[] a) throws JSONException {
        JSONObject obj=new JSONObject();
          obj.put("name","foo");
          obj.put("num",new Integer(100));
          obj.put("balance",new Double(1000.21));
          obj.put("is_vip",new Boolean(true));
//          obj.put("nickname",null);

        Event e  = new Event();
        e.setChildId(1l);
        e.setEventTime(new Date());
        e.setGroupId(2l);
        e.setParentId(3l);
        e.setPracticeId(4l);
        e.setQuestionId(5l);
        e.setType(EventType.PAUSE_PRACTICE);
        e.setValue("hellow");
        EventsDto eventsDto = new EventsDto(Lists.newArrayList(e));
        Gson gson = new Gson();
        String json = gson.toJson(eventsDto);



          System.out.print(json);
    }

    @Test
    public void simpletest() throws JSONException {
        Event e  = new Event();
        e.setChildId(1l);
        e.setEventTime(new Date());
        e.setGroupId(2l);
        e.setParentId(3l);
        e.setPracticeId(4l);
        e.setQuestionId(5l);
        e.setType(EventType.PAUSE_PRACTICE);
        e.setValue("hellow");
        EventsDto eventsDto = new EventsDto(Lists.newArrayList(e));
        Gson gson = new Gson();
        String json = gson.toJson(eventsDto);


          System.out.print(json);
    }
}

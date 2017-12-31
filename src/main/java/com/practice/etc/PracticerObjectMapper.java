package com.practice.etc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

import java.text.SimpleDateFormat;

/**
 * User: tomer
 */
public class PracticerObjectMapper extends ObjectMapper {

    public PracticerObjectMapper() {
        registerModule(new Hibernate4Module());
        setDateFormat(new SimpleDateFormat("MM/dd/yyyy HH:mm"));
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
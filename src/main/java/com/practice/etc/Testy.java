package com.practice.etc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: tomer
 */
public class Testy {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1977);
        calendar.set(Calendar.MONTH, 9);
        calendar.set(Calendar.DAY_OF_MONTH, 9);
        System.out.println(simpleDateFormat.format(calendar.getTime()));

        Date date = simpleDateFormat.parse(simpleDateFormat.format(calendar.getTime()));
        System.out.println(date);
    }
}

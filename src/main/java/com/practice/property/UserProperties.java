package com.practice.property;

import com.practice.model.UserProperty.PropertyKey;

/**
 * Created by Tomer
 */
public class UserProperties {
    public static final PropertyKey<Boolean> RECEIVE_DAILY_MAIL_KEY = new PropertyKey<>("RECEIVE_DAILY_MAIL_KEY", Boolean.class, true);
    public static final PropertyKey<Integer> DAILY_MAIL_HOUR_KEY = new PropertyKey<>("DAILY_MAIL_HOUR_KEY", Integer.class, 20);
}

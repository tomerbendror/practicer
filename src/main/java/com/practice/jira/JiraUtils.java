package com.practice.jira;

/**
 * Created by Tomer
 */
public class JiraUtils {

    public static final int MINUTE_IN_SECONDS = 60;
    public static final int HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60;
    public static final int DAY_IN_SECONDS = HOUR_IN_SECONDS * 8;

    public static String getTotalTimeStr(int totalTimeSecond) {
        int days = totalTimeSecond / DAY_IN_SECONDS;
        totalTimeSecond = totalTimeSecond % DAY_IN_SECONDS;

        int hours = totalTimeSecond / HOUR_IN_SECONDS;
        totalTimeSecond = totalTimeSecond % HOUR_IN_SECONDS;

        int minutes = totalTimeSecond / MINUTE_IN_SECONDS;
        return (days > 0 ? days + "d " : "") + (hours > 0 ? hours + "h " : "") + (minutes > 0 ? minutes + "m" : "");
    }
}
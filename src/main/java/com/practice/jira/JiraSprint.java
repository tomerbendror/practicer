package com.practice.jira;


import com.practice.jira.enums.SprintState;

import java.util.List;

/**
 * Created by Tomer.Ben-Dror on 2/12/2017
 */
public class JiraSprint {
    private int id;
    private String name;
    private SprintState state;
    private List<Integer> issuesIds;
    private SprintTimeRemaining timeRemaining;

    public JiraSprint() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SprintState getState() {
        return state;
    }

    public void setState(SprintState state) {
        this.state = state;
    }

    public List<Integer> getIssuesIds() {
        return issuesIds;
    }

    public void setIssuesIds(List<Integer> issuesIds) {
        this.issuesIds = issuesIds;
    }

    public SprintTimeRemaining getTimeRemaining() {
        return timeRemaining;
    }

    public String getWorkingDaysRemaining() {
        return getDaysRemainingInt() + " days left";
    }

    private int getDaysRemainingInt() {
        String text = timeRemaining.getText();
        String daysStr = text.substring(0, text.indexOf(" days left"));
        Integer daysInt = Integer.valueOf(daysStr);
//        return daysInt <= 7 ? daysInt -2 : daysInt -4;
        return daysInt;
    }

    public int getDaysRemainingSeconds() {
        return getDaysRemainingInt() * 60 * 60 * 8;
    }

    public void setTimeRemaining(SprintTimeRemaining timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}

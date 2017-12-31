package com.practice.jira;

import java.util.List;

/**
 * Created by Tomer.Ben-Dror on 2/13/2017
 */
public class JiraStoryDetails {
    private List<JiraSubtask> subtasks;

    public JiraStoryDetails() {
    }

    public List<JiraSubtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<JiraSubtask> subtasks) {
        this.subtasks = subtasks;
    }
}

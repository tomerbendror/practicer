package com.practice.dto;

import com.practice.model.ParentsGroup;
import com.practice.model.Practice;
import com.practice.model.PracticeToGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Tomer
 */
public class GroupPracticesDto {
    private Long groupId;
    private String groupName;
    private String groupDescription;
    private List<Long> practicesIds;

    public GroupPracticesDto(ParentsGroup group) {
        this.groupId = group.getId();
        this.groupName = group.getName();
        this.groupDescription = group.getDescription();
        Set<PracticeToGroup> practicesToGroups = group.getPractices();
        practicesIds = new ArrayList<>(practicesToGroups.size());
        for (PracticeToGroup practiceToGroup : practicesToGroups) {
            Practice practice = practiceToGroup.getPractice();
            practicesIds.add(practice.getId());
        }
    }

    public GroupPracticesDto() {

    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public List<Long> getPracticesIds() {
        return practicesIds;
    }

    public void setPracticesIds(List<Long> practicesIds) {
        this.practicesIds = practicesIds;
    }
}

package com.practice.dto;

import com.practice.model.ChildUser;
import com.practice.model.Gender;
import com.practice.model.Practice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tomer
 */
public class ChildPracticesDto {
    private Long childId;
    private String childFirstName;
    private String childLastName;
    private Gender gender;
    private List<Long> practicesIds;

    public ChildPracticesDto(ChildUser child, Collection<Practice> practices) {
        this.childId = child.getId();
        this.childFirstName = child.getFirstName();
        this.childLastName = child.getLastName();
        this.gender = child.getGender();

        practicesIds = new ArrayList<>(practices.size());
        for (Practice practice : practices) {
            practicesIds.add(practice.getId());
        }
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getChildFirstName() {
        return childFirstName;
    }

    public void setChildFirstName(String childFirstName) {
        this.childFirstName = childFirstName;
    }

    public String getChildLastName() {
        return childLastName;
    }

    public void setChildLastName(String childLastName) {
        this.childLastName = childLastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Long> getPracticesIds() {
        return practicesIds;
    }

    public void setPracticesIds(List<Long> practicesIds) {
        this.practicesIds = practicesIds;
    }
}

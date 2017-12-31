package com.practice.dto.group;

import com.practice.model.ChildUser;
import com.practice.model.Gender;

/**
 * Created by Tomer
 */
public class ChildInGroupDto {
    private Long childId;
    private String firstName;
    private Gender gender;
    private Boolean inGroup;

    public ChildInGroupDto() {
    }

    public ChildInGroupDto(ChildUser childUser, boolean inGroup) {
        this.childId = childUser.getId();
        this.firstName = childUser.getFirstName();
        this.inGroup = inGroup;
        this.gender = childUser.getGender();
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Boolean getInGroup() {
        return inGroup;
    }

    public void setInGroup(Boolean inGroup) {
        this.inGroup = inGroup;
    }
}

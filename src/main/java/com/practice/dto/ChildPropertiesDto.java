package com.practice.dto;

import com.practice.model.ChildUser;
import com.practice.model.Gender;

/**
 * Created by Tomer
 */
public class ChildPropertiesDto {
    private Long childId;
    private String firstName;
    private String lastName;
    private String userAvatar;
    private Gender gender;

    public ChildPropertiesDto() {
    }

    public ChildPropertiesDto(ChildUser child) {
        this.childId = child.getId();
        this.firstName = child.getFirstName();
        this.lastName = child.getLastName();
        this.userAvatar = child.getProfileImageUrl();
        this.gender = child.getGender();
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}

package com.practice.dto.group;

import com.practice.model.*;

import java.util.Date;

/**
 * User: tomer
 */
public class ParentInGroupDto {
    private Long invitationId;
    private Long userId;
    private String email;
    private String fullName;
    private Boolean manager;
    private Boolean teacher;
    private Date joinDate;    // invite date in case of user that hasn't join yet
    private Boolean member;
    private String profileImageUrl;
    private Gender gender;

    public ParentInGroupDto() {
    }

    // for a registered user
    public ParentInGroupDto(ParentToGroup parentToGroup) {
        userId = parentToGroup.getParentUser().getId();
        ParentUser parentUser = parentToGroup.getParentUser();
        email = parentUser.getEmail();
        fullName = parentUser.getFullName();
        joinDate = parentToGroup.getJoinDate();
        manager = parentToGroup.getUserInGroupRole().isManager();
        teacher = parentToGroup.getUserInGroupRole() == UserInGroupRole.TEACHER;
        profileImageUrl = parentUser.getProfileImageUrlOrDefault();
        member = true;
        gender = parentUser.getGender();
    }

    // for invitee that hasn't join the group yet
    public ParentInGroupDto(GroupInvitation groupInvitation) {
        invitationId = groupInvitation.getId();
        email = groupInvitation.getEmail();
        joinDate = groupInvitation.getInviteDate();
        profileImageUrl = new ParentUser().getProfileImageUrlOrDefault();
        member = false;
        manager = false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getManager() {
        return manager;
    }

    public void setManager(Boolean manager) {
        this.manager = manager;
    }

    public Boolean getTeacher() {
        return teacher;
    }

    public void setTeacher(Boolean teacher) {
        this.teacher = teacher;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Boolean getMember() {
        return member;
    }

    public void setMember(Boolean member) {
        this.member = member;
    }

    public Long getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}

package com.practice.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * User: tomer
 */
@Entity
@Table(name="parents_group")
public class ParentsGroup extends BaseEntity {
    @NotNull
    @Length(min=1, max=100)
    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentsGroup", cascade = CascadeType.ALL)
    private Set<ParentToGroup> groupsToUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentsGroup", cascade = CascadeType.ALL)
    private Set<ChildToGroup> childsToGroup = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentsGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupInvitation> groupInvitations = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentsGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PracticeToGroup> practices = new HashSet<>();

    @Column(name = "CREATED_DATE", updatable = false, insertable = false)
    private Date createdDate;

    public ParentsGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ParentToGroup> getGroupsToUsers() {
        return groupsToUsers;
    }

    public void setGroupsToUsers(Set<ParentToGroup> groupsToUsers) {
        this.groupsToUsers = groupsToUsers;
    }

    public Set<GroupInvitation> getGroupInvitations() {
        return groupInvitations;
    }

    public void setGroupInvitations(Set<GroupInvitation> groupInvitations) {
        this.groupInvitations = groupInvitations;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Set<PracticeToGroup> getPractices() {
        return practices;
    }

    public void setPractices(Set<PracticeToGroup> practices) {
        this.practices = practices;
    }

    public Set<ChildToGroup> getChildsToGroup() {
        return childsToGroup;
    }

    public void setChildsToGroup(Set<ChildToGroup> childsToUsers) {
        this.childsToGroup = childsToUsers;
    }

    public List<ParentUser> getTeachers() {
        List<ParentUser> teachers = new LinkedList<>();
        for (ParentToGroup groupsToUser : groupsToUsers) {
            if (groupsToUser.getUserInGroupRole() == UserInGroupRole.TEACHER) {
                teachers.add(groupsToUser.getParentUser());
            }
        }
        return teachers;
    }
}

package com.practice.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * User: tomer
 */
@Entity
@Table(name="parent_to_group")
public class ParentToGroup extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", nullable = false)
    private ParentUser parentUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private ParentsGroup parentsGroup;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "USER_IN_GROUP_ROLE", nullable = false)
    private UserInGroupRole userInGroupRole;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "JOIN_DATE")
    private Date joinDate;

    public ParentToGroup() {
    }

    public ParentToGroup(ParentUser parentUser, ParentsGroup parentsGroup, UserInGroupRole userInGroupRole) {
        this.parentUser = parentUser;
        this.parentsGroup = parentsGroup;
        this.userInGroupRole = userInGroupRole;
        joinDate = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentToGroup that = (ParentToGroup) o;
        return Objects.equals(getParentUser(), that.getParentUser()) &&
                Objects.equals(getParentsGroup(), that.getParentsGroup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getParentUser(), getParentsGroup());
    }

    public ParentUser getParentUser() {
        return parentUser;
    }

    public void setParentUser(ParentUser parentUser) {
        this.parentUser = parentUser;
    }

    public ParentsGroup getParentsGroup() {
        return parentsGroup;
    }

    public void setParentsGroup(ParentsGroup parentsGroup) {
        this.parentsGroup = parentsGroup;
    }

    public UserInGroupRole getUserInGroupRole() {
        return userInGroupRole;
    }

    public void setUserInGroupRole(UserInGroupRole userInGroupRole) {
        this.userInGroupRole = userInGroupRole;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
}

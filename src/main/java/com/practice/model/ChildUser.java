package com.practice.model;

import com.practice.model.statistics.PracticeResult;
import com.practice.security.PracticerAuthority;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: tomer
 */
@Entity
@Table(name="child_user")
public class ChildUser extends User {

    public ChildUser() {
    }

    @ManyToMany(mappedBy="childs")
    private Set<ParentUser> parents = new HashSet<>(0);

    @ManyToMany(mappedBy="childs")
    private Set<Practice> practices = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "childUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChildToGroup> childToGroups = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "childUser", cascade = CascadeType.ALL)
    private List<PracticeResult> practiceResults = new ArrayList<>(0);

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = super.getAuthorities();
        authorities.add(PracticerAuthority.CHILD.role);
        return authorities;
    }

    @Override
    public String getActualUserName() {
        return getUserName();
    }

    public Set<ParentUser> getParents() {
        return parents;
    }

    @Override
    public void setUserName(String userName) {
        if (userName.contains("@")) {
            throw new UnsupportedOperationException("Child userName cannot contain the '@' char");
        }
        super.setUserName(userName);
    }

    public Set<ChildToGroup> getChildToGroups() {
        return childToGroups;
    }

    public void setChildToGroups(Set<ChildToGroup> childToGroups) {
        this.childToGroups = childToGroups;
    }

    public boolean isChildOf(Long parentId) {
        for (ParentUser parent : parents) {
            if (parent.getId().equals(parentId)) {
                return true;
            }
        }
        return false;
    }

    public Set<Practice> getPractices() {
        return practices;
    }

    public void setPractices(Set<Practice> practices) {
        this.practices = practices;
    }
}

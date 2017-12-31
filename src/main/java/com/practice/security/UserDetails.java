package com.practice.security;

import com.practice.model.Gender;
import com.practice.model.User;

/**
 * User: tomer
 */
public class UserDetails extends org.springframework.security.core.userdetails.User {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;

    public UserDetails(User user) {
        super(user.getActualUserName(), user.getPassword(), user.getAuthorities());
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.gender = user.getGender();
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isParent() {
        return getAuthorities().contains(PracticerAuthority.PARENT.role);
    }

    public boolean isChild() {
        return getAuthorities().contains(PracticerAuthority.CHILD.role);
    }

    public Gender getGender() {
        return gender;
    }
}

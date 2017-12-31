package com.practice.model;

/**
 * User: tomer
 */
public enum UserInGroupRole {
    TEACHER,
    MANAGER,
    MEMBER,
    DELETED;
    
    public boolean isManager() {
        return this == MANAGER || this == TEACHER;
    }
}

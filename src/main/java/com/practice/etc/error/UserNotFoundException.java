package com.practice.etc.error;

/**
 * Created by Tomer
 */
public class UserNotFoundException extends LogicalException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

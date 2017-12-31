package com.practice.repository;

/**
 * User: tomer
 */
public class Empty {
    private static Empty empty = new Empty();

    private Empty() {
    }

    public static Empty getInstance() {
        return empty;
    }
}

package com.namodu.pustakam.exception;

/**
 * Created by sanemdeepak on 7/17/16.
 */
public class DuplicateIdException extends Exception {

    private DuplicateIdException() {
    }

    public DuplicateIdException(String exceptionDesc) {

        super("Duplicate unique identifier found: " + exceptionDesc);
    }
}

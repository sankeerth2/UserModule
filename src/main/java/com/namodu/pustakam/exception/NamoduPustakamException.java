package com.namodu.pustakam.exception;

/**
 * Created by sanemdeepak on 11/20/16.
 */
public class NamoduPustakamException extends RuntimeException {

    private NamoduPustakamException() {
    }

    public NamoduPustakamException(String message) {
        super(message);
    }

    public NamoduPustakamException(String message, Throwable cause) {
        super(message, cause);
    }
}

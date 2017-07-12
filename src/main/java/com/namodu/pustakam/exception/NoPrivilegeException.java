package com.namodu.pustakam.exception;

/**
 * Created by sanemdeepak on 11/20/16.
 */
public class NoPrivilegeException extends NamoduPustakamException {

    public NoPrivilegeException(String message) {
        super(message);
    }

    public NoPrivilegeException(String message, Throwable cause) {
        super(message, cause);
    }
}

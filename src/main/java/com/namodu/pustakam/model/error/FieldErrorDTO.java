package com.namodu.pustakam.model.error;

/**
 * Created by sanemdeepak on 11/11/16.
 */
public class FieldErrorDTO {

    private String path;
    private String message;

    public FieldErrorDTO(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }
}

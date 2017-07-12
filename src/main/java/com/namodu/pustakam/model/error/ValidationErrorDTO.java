package com.namodu.pustakam.model.error;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanemdeepak on 11/11/16.
 */
public class ValidationErrorDTO {

    private final String code = HttpStatus.BAD_REQUEST.name();

    private final List<FieldErrorDTO> fieldErrors = new ArrayList<>();

    public ValidationErrorDTO() {
    }

    public void addFieldError(String field, String message) {
        FieldErrorDTO error = new FieldErrorDTO(field, message);
        fieldErrors.add(error);
    }

    public String getCode() {
        return code;
    }

    public List<FieldErrorDTO> getFieldErrors() {
        return fieldErrors;
    }
}

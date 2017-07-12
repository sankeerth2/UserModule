package com.namodu.pustakam.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by sanemdeepak on 10/5/16.
 */
public class JwtTokenValidationException extends AuthenticationException {

    public JwtTokenValidationException(String msg) {
        super(msg);
    }
}

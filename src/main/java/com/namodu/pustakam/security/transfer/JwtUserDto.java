package com.namodu.pustakam.security.transfer;

/**
 * Simple placeholder for info extracted from the JWT
 *
 * @author pascal alma
 */
public class JwtUserDto {


    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
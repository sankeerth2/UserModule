package com.namodu.pustakam.security.util;

import com.namodu.pustakam.security.transfer.JwtUserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * convenience class to generate a token for testing your requests.
 * Make sure the used secret here matches the on in your application.yml
 *
 * @author pascal alma
 */
public class JwtTokenGenerator {

    /**
     * Generates a JWT token containing username as subject, and userId and role as additional claims. These properties are taken from the specified
     * User object. Tokens validity is infinite.
     *
     * @param u the user for which the token will be generated
     * @return the JWT token
     */
    public static String generateToken(JwtUserDto u, String secret) {
        Claims claims = Jwts.claims().setSubject(u.getUsername());
        claims.put("username", u.getUsername());
        claims.put("password", u.getPassword());

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        JwtUserDto user = new JwtUserDto();
        user.setUsername("dsanem");
        user.setPassword("letsdoit");
        System.out.println("**************************************\n\n" + generateToken(user, "my-very-secret-key") + "\n\n**************************************");
    }
}

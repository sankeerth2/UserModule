package com.namodu.pustakam.security.util;

import com.namodu.pustakam.security.transfer.JwtUserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class JwtTokenValidator {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    @Value("${jwt.secret}")
    private String secret;

    public JwtUserDto parseToken(String token) {

        if (token.startsWith("Basic")) {
            return base64Decoder(token);
        } else if (token.startsWith("EFSR")) {
            return jwtDecoder(token);
        } else {
            return null;
        }
    }

    public JwtUserDto base64Decoder(String token) {
        JwtUserDto jwtUserDto = new JwtUserDto();
        token = StringUtils.substringAfter(token, "Basic").trim();
        byte[] byteArray = Base64.decodeBase64(token);
        String decodedString = new String(byteArray);
        String userName = decodedString.substring(0, decodedString.indexOf(":", 0));
        String password = decodedString.substring(decodedString.indexOf(":", 0) + 1, decodedString.length());
        jwtUserDto.setUsername(userName);
        jwtUserDto.setPassword(password);
        return jwtUserDto;
    }

    public JwtUserDto jwtDecoder(String token) {
        token = StringUtils.substringAfter(token, "EFSR").trim();
        byte[] byteArray = Base64.decodeBase64(token);
        token = new String(byteArray);
        JwtUserDto u = null;
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            Optional userName = Optional.ofNullable((body.getSubject()));
            Optional password = Optional.ofNullable((String) body.get("password"));
            if (userName.isPresent() && password.isPresent()) {
                u = new JwtUserDto();
                u.setUsername(body.getSubject());
                u.setPassword((String) body.get("password"));
            }

        } catch (JwtException e) {
            // Simply print the exception and null will be returned for the userDto
            logger.info("Token validation exception: " + e.getMessage());
        } finally {
            return u;
        }
    }
}

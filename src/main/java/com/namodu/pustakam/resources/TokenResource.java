package com.namodu.pustakam.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namodu.pustakam.model.RequestContext;
import com.namodu.pustakam.security.transfer.JwtUserDto;
import com.namodu.pustakam.security.util.JwtTokenGenerator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by sanemdeepak on 10/10/16.
 */
@RestController
@RequestMapping("/api")
public class TokenResource {

    private final Logger logger = LoggerFactory.getLogger(TokenResource.class);

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    ObjectMapper mapper;

    @RequestMapping(
            value = "/token",
            method = RequestMethod.POST, consumes = "application/json",
            produces = "application/json"
    )
    @ResponseBody
    public ResponseEntity<String> login(@RequestHeader(value = "Authorization") String authHeader) throws IOException {
        RequestContext context = new RequestContext();
        context.setCorrelationId(generateRandomId());

        JwtUserDto jwtUserDto = new JwtUserDto();
        String token = StringUtils.substringAfter(authHeader, "Basic").trim();
        byte[] byteArray = Base64.decodeBase64(token);
        String decodedString = new String(byteArray);
        String userName = decodedString.substring(0, decodedString.indexOf(":", 0));
        String password = decodedString.substring(decodedString.indexOf(":", 0) + 1, decodedString.length());
        jwtUserDto.setUsername(userName);
        jwtUserDto.setPassword(password);
        String encodedToken = Base64.encodeBase64String(JwtTokenGenerator.generateToken(jwtUserDto, secret).getBytes());
        return new ResponseEntity<>("{\"token\":\"" + encodedToken + "\"}", HttpStatus.OK);
    }

    //helpers
    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }
}

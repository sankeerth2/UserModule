package com.namodu.pustakam.resources;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by sanemdeepak on 10/19/16.
 */
@RestController
@RequestMapping("api")
public class PingResource {

    @RequestMapping(
            value = "/ping",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    //@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
    @ResponseBody
    public ResponseEntity<String> ping() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Access-Control-Allow-Origin", "*");
        return new ResponseEntity<>("{\"response\":\"Pong\"}", httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/secured/ping",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    public ResponseEntity<String> secPing() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Access-Control-Allow-Origin", "*");
        return new ResponseEntity<>("{\"response\":\"Secured ping\"}", httpHeaders, HttpStatus.OK);
    }

}

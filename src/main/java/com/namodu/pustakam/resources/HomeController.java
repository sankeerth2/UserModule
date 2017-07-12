package com.namodu.pustakam.resources;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by sanemdeepak on 10/31/16.
 */
@Controller
@RequestMapping(value = "/webapp")
public class HomeController {

    @RequestMapping(value = "/login")
    public String login() {
        return "/index.html";
    }

    @RequestMapping(value = "/users")
    public String users() {
        return "/usrmgmt.html";
    }

    @RequestMapping(value = "/signup")
    public String signup() {
        return "/signup.html";
    }
}

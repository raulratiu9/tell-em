package com.tellem.controller.authentication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/loginSuccess")
    public String loginSuccess() {
        return "loginSuccess";
    }
}

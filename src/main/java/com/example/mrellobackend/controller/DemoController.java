package com.example.mrellobackend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/api/v1/demo")
    public String sayHello(@AuthenticationPrincipal UserDetails userDetails) {
        return "Hello, " + userDetails.getUsername() + "!";
    }
}
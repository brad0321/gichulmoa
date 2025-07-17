package com.pro.project01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuestController {

    @GetMapping("/guest/start")
    public String guestStart() {
        return "guest/start";
    }
}

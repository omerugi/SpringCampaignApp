package com.example.mabaya.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class BaseController {
    @GetMapping
    public String helloWorld(){
        return "This is the base url for more details - https://github.com/omerugi/SpringCampaignApp";
    }
}

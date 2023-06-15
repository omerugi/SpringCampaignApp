package com.example.mabaya.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class BaseController {

    @GetMapping
    public String helloWorld(){
        return "hello world";
    }
}

package com.example.mabaya.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class BaseController {
    @GetMapping
    public String helloWorld(){
        return "<html><head><title>Base URL Info</title><style>body {font-family: Arial, sans-serif;}</style></head><body><p>This is the base url:</p><ul><li><a href='https://github.com/omerugi/SpringCampaignApp'>Github repo</a></li><li><a href='"+System.getenv("swagger_ui_url")+"/swagger-ui/index.html'>Swagger UI</a></li></ul></body></html>";
    }
}

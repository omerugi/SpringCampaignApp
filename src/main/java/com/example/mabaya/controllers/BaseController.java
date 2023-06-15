package com.example.mabaya.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class BaseController {
    @Value("${myproperty.swagger_url}")
    private String swaggerUrl;

    @GetMapping
    public String helloWorld(){
        return "<html><head>\n" +
                "  <title>Base URL</title>\n" +
                "  <style>\n" +
                "body {font-family: Arial, sans-serif;}\n"+
                "    .symbol-container {\n" +
                "      display: flex;\n" +
                "      flex-direction: row;\n" +
                "      justify-content: center;\n" +
                "      align-items: center;\n" +
                "      gap: 20px;\n" +
                "      margin-top: 20px;\n" +
                "    }\n" +
                "\n" +
                "    .symbol {\n" +
                "      display: flex;\n" +
                "      flex-direction: column;\n" +
                "      align-items: center;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <h1>Base URL</h1>\n" +
                "  \n" +
                "  <div class=\"symbol-container\">\n" +
                "    <div class=\"symbol\">\n" +
                "      <a href=\"https://github.com/omerugi/SpringCampaignApp\">\n" +
                "        <img src=\"https://cdn-icons-png.flaticon.com/512/25/25231.png\" alt=\"GitHub\" width=\"100\" height=\"100\">\n" +
                "      </a>\n" +
                "      <p>GitHub</p>\n" +
                "    </div>\n" +
                "    \n" +
                "    <div class=\"symbol\">\n" +
                "      <a href=\""+ swaggerUrl +"/swagger-ui/index.html\">\n" +
                "        <img src=\"data:image/png;base64,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\" alt=\"Swagger UI\" width=\"100\" height=\"100\">\n" +
                "      </a>\n" +
                "      <p>Swagger UI</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body></html>";
    }
}

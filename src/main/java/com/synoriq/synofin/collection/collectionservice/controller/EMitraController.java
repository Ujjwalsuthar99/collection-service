package com.synoriq.synofin.collection.collectionservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RestController
@RequestMapping("/v1")
public class EMitraController {

    @Autowired
    TemplateEngine templateEngine;

    @GetMapping("/e-mitra")
    public String emitra() {
        return templateEngine.process("emitra", new Context());
    }

}

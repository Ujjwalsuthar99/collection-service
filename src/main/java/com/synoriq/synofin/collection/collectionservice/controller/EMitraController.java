package com.synoriq.synofin.collection.collectionservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1")
public class EMitraController {

    @GetMapping("/e-mitra")
    public String emitra() {
        return "emitra";
    }

}

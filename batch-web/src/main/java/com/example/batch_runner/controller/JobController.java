package com.example.batch_runner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JobController {

    @GetMapping("/job")
    public String job() {
        return "job";
    }
}

package com.sparkLab.study;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/pingTest")
    public String test() {
        return "pingTest";
    }
}

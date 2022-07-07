package com.example.suzumechat.service.utility;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtilityController {
    @GetMapping("/healthcheck")
    public int healthcheck() {
        return 1;
    }
}

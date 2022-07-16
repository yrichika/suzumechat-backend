package com.example.suzumechat.service.utility;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtilityController {
    @GetMapping("/healthcheck")
    public int healthcheck() {
        return 1;
    }

    // TODO:
    // @GetMapping("/csrfToken")
    // public CsrfToken csrfToken(CsrfToken token) {
  	//     return token;
    // }
}

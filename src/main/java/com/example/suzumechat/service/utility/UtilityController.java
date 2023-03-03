package com.example.suzumechat.service.utility;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtilityController {
    // Keeping this commented out for a while. Sometimes this endpoint is handy to test something.
    // @GetMapping("/healthcheck")
    // public ResponseEntity<String> healthcheck() {
    //     return ResponseEntity.status(HttpStatus.OK).build();
    // }

    // TEST:
    @GetMapping("/csrfToken")
    public CsrfToken csrfToken(CsrfToken token) {
        return token;
    }
}

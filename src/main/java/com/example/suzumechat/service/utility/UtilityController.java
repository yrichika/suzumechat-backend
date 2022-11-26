package com.example.suzumechat.service.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtilityController {
    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/csrfToken")
    public CsrfToken csrfToken(CsrfToken token) {
        return token;
    }
}

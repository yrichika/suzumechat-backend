package com.example.suzumechat.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import org.springframework.stereotype.Component;


@Component
public class Hash {

    private MessageDigest digest;

    public Hash() throws Exception {
        digest = MessageDigest.getInstance("SHA-256");
    }

    public String digest(String text) {
        byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytes);
    }
}

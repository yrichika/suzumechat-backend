package com.example.suzumechat.utility;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class Random {
    private SecureRandom random = new SecureRandom();

    public String alphanumeric() {
        return alphanumeric(64);
    }

    public String alphanumeric(Integer length) {
        return new BigInteger(length * 5, random).toString(32);
    }
}

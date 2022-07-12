package com.example.suzumechat.testutil.random.pattern;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

public class RandomBoolean {

    private final Random random;
    
    public RandomBoolean(Random random) {
        this.random = random;
    }

    public Boolean nextBoolean() {
        return random.nextBoolean();
    }
}

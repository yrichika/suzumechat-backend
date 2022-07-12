package com.example.suzumechat.testutil.random;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.suzumechat.testutil.random.pattern.RandomBoolean;
import com.example.suzumechat.testutil.random.pattern.RandomInteger;
import com.example.suzumechat.testutil.random.pattern.RandomString;

import lombok.Getter;
import lombok.experimental.Accessors;

public class TestRandom {

    private final Random random = new Random();
    public final RandomString string = new RandomString(random);
    public final RandomInteger integer = new RandomInteger(random);
    public final RandomBoolean bool = new RandomBoolean(random);

}

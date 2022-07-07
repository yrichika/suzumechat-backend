package com.example.suzumechat.testutil.random;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.suzumechat.testutil.random.pattern.RandomInteger;
import com.example.suzumechat.testutil.random.pattern.RandomString;

import lombok.Getter;
import lombok.experimental.Accessors;

public class TestRandom {

    public final RandomString string = new RandomString();

    public final RandomInteger integer = new RandomInteger();

}

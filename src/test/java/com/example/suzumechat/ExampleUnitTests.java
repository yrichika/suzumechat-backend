package com.example.suzumechat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.suzumechat.testconfig.TestConfig;

import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
public class ExampleUnitTests {
    @Test
    void example() {

    }
}

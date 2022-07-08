package com.example.suzumechat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import com.example.suzumechat.testconfig.TestConfig;

import lombok.val;

@TestConfiguration
@Import(TestConfig.class)
public class ExampleUnitTests {
    @Test
    void example() {

    }
}

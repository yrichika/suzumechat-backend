package com.example.suzumechat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.testconfig.TestConfig;
import lombok.SneakyThrows;

@SpringJUnitConfig
@Import(TestConfig.class)
public class ExampleUnitTests {

    @SneakyThrows
    @Test
    void example() {

    }
}

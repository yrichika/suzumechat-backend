package com.example.suzumechat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.testconfig.TestConfig;
import lombok.SneakyThrows;
import lombok.*;

@SpringJUnitConfig
@Import(TestConfig.class)
public class ExampleUnitTests {

    @SneakyThrows
    @Test
    void example() {

    }
}

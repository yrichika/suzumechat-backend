package com.example.suzumechat.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import static org.assertj.core.api.Assertions.*;

import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
public class HashTests {

    private Hash hash;

    @Autowired
    private TestRandom random;

    @BeforeEach
    public void setUp() throws Exception {
        hash = new Hash();
    }

    @Test
    public void digest_should_return_() {
        val plainText = random.string.alphanumeric();

        String hashedFirst = hash.digest(plainText);
        String hashedSecond = hash.digest(plainText);
   
        assertThat(hashedFirst).isNotEqualTo(plainText);
        assertThat(hashedFirst).isEqualTo(hashedSecond);
    }
}

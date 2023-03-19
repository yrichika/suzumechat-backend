package com.example.suzumechat.utility;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
public class CrypterTests {

    private Crypter crypter;

    @Autowired
    private TestRandom random;

    @BeforeEach
    public void setUp() throws Exception {
        crypter = new Crypter("plain_keyset.json", "enc_keyset.json", "");
    }

    @Test
    public void encrypt_and_decrypt_should_encrypt_and_decrypt() throws Exception {
        val plainText = random.string.alphanumeric();
        val ad = random.string.alphanumeric();

        val encryptedBytes = crypter.encrypt(plainText, ad);
        assertThat(encryptedBytes.toString()).isNotEqualTo(plainText);

        val decryptedString = crypter.decrypt(encryptedBytes, ad);
        assertThat(decryptedString).isEqualTo(plainText);
    }
}

package com.example.suzumechat.utility;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.integration.awskms.AwsKmsClient;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Crypter {

    private Aead aead;

    private @Nullable String plainKeysetFilename;
    private @Nullable String encKeysetFilePath;
    private @Nullable String masterKeyUrl; // "aws-kms://arn:aws:kms:us-east-1:....";

    public Crypter(
        @Value("${tink.plain-keyset-file}") String plainKeysetFilename,
        @Value("${tink.enc-keyset-file}") String encKeysetFilePath,
        @Nullable @Value("${tink.master-key-uri}") String masterKeyUrl) throws Exception {

        this.plainKeysetFilename = plainKeysetFilename;
        this.encKeysetFilePath = encKeysetFilePath;
        this.masterKeyUrl = masterKeyUrl;

        AeadConfig.register();
        aead = keysetHandle().getPrimitive(Aead.class);
    }

    public byte[] encrypt(String plaintext, String associatedData) throws Exception {
        return aead.encrypt(plaintext.getBytes(), associatedData.getBytes());
    }

    public String decrypt(byte[] cipherText, String associatedData) throws Exception {
        val decryptedBytes = aead.decrypt(cipherText, associatedData.getBytes());
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private KeysetHandle keysetHandle() throws Exception {
        if (Objects.isNull(masterKeyUrl) || masterKeyUrl.isEmpty()) {
            log.info("Encrption with the plain keyset.");
            return CleartextKeysetHandle
                .read(JsonKeysetReader.withFile(new File(plainKeysetFilename)));
        }
        log.info("Encrption with the encrypted keyset.");
        return KeysetHandle.read(JsonKeysetReader.withFile(new File(encKeysetFilePath)),
            new AwsKmsClient().getAead(masterKeyUrl) // FIXME:
        );
    }
}

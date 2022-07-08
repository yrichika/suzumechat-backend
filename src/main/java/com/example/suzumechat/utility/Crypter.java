package com.example.suzumechat.utility;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.integration.awskms.AwsKmsClient;

import lombok.val;

@Component
public class Crypter {

    private Aead aead;

    private String plainKeysetFilename = "plain_keyset.json"; // TODO: specify file from config file
    private String encKeysetFilePath = "enc_keyset.json"; // TODO: specify file from config file
    // TODO: 一旦nullにしてある
    private String masterKeyUrl = null; // "aws-kms://arn:aws:kms:us-east-1:....";

    public Crypter() throws Exception {
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
        if (Objects.isNull(masterKeyUrl)) {
            return CleartextKeysetHandle.read(
                JsonKeysetReader.withFile(new File(plainKeysetFilename))
            );
        }

        return KeysetHandle.read(
            JsonKeysetReader.withFile(new File(encKeysetFilePath)),
            new AwsKmsClient().getAead(masterKeyUrl) // FIXME:
        );
    }
}

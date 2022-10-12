package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.guest.dto.message.JoinRequest;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;

import lombok.*;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class JoinRequestFactory {
    @Autowired
    private TestRandom random;

    private String visitorId = null;
    private String codename = null;
    private String passphrase = null;

    public JoinRequest make() {
        val visitorId = TestHelper.getOrDefault(this.visitorId,
                random.string.alphanumeric());
        val codename =
                TestHelper.getOrDefault(this.codename, random.string.alphanumeric());
        val passphrase = TestHelper.getOrDefault(this.passphrase,
                random.string.alphanumeric());

        val joinRequest = new JoinRequest(visitorId, codename, passphrase);

        reset();
        return joinRequest;
    }

    private void reset() {
        visitorId = null;
        codename = null;
        passphrase = null;
    }
}

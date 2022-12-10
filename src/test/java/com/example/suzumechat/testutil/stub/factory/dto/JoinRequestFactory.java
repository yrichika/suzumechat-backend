package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.guest.dto.message.JoinRequest;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class JoinRequestFactory {
    @Autowired
    private TestRandom random;

    private String visitorId = null;
    private String visitorPublicKey = null;
    private String whoIAmEnc = null;

    public JoinRequest make() {
        val visitorId = TestHelper.getOrDefault(this.visitorId,
            random.string.alphanumeric());
        val visitorPublicKey = TestHelper.getOrDefault(this.visitorPublicKey, random.string.alphanumeric());
        val whoIAmEnc =
            TestHelper.getOrDefault(this.whoIAmEnc, random.string.alphanumeric());

        val joinRequest = new JoinRequest(visitorId, visitorPublicKey, whoIAmEnc);

        reset();
        return joinRequest;
    }

    private void reset() {
        visitorId = null;
        visitorPublicKey = null;
        whoIAmEnc = null;
    }
}

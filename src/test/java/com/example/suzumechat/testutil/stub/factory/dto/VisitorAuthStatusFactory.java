package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.channel.dto.message.VisitorAuthStatus;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.val;

public class VisitorAuthStatusFactory {
    @Autowired
    private TestRandom random;

    private String visitorId;
    private Boolean isAuthenticated;

    public VisitorAuthStatus make() {
        val visitorId = TestHelper.getOrDefault(this.visitorId, random.string.alphanumeric());
        val isAuthenticated =
            TestHelper.getOrDefault(this.isAuthenticated, random.bool.nextBoolean());
        val visitorsAuthStatus = new VisitorAuthStatus(visitorId, isAuthenticated);
        reset();
        return visitorsAuthStatus;
    }

    private void reset() {
        visitorId = null;
        isAuthenticated = null;
    }
}

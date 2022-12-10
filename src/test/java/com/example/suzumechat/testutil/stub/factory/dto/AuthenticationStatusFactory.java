package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.guest.dto.message.AuthenticationStatus;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class AuthenticationStatusFactory {

    @Autowired
    private TestRandom random;

    private boolean isClosed = false;
    private Boolean isAuthenticated = null;
    private String guestId = null;
    private String guestChannelToken = null;
    private String channelName = null;
    private String secretKey = null;

    public AuthenticationStatus make() {

        val isClosed =
            TestHelper.getOrDefault(this.isClosed, random.bool.nextBoolean());
        val isAuthenticated = TestHelper.getOrDefault(this.isAuthenticated,
            random.bool.nextBoolean());
        val guestId =
            TestHelper.getOrDefault(this.guestId, random.string.alphanumeric());
        val guestChannelToken = TestHelper.getOrDefault(this.guestChannelToken,
            random.string.alphanumeric());
        val channelName = TestHelper.getOrDefault(this.channelName,
            random.string.alphanumeric());
        val secretKey = TestHelper.getOrDefault(this.secretKey,
            random.string.alphanumeric());

        val authenticationStatus =
            new AuthenticationStatus(isClosed, isAuthenticated, guestId,
                guestChannelToken, channelName, secretKey);
        reset();
        return authenticationStatus;
    }

    private void reset() {
        isClosed = false;
        isAuthenticated = null;
        guestId = null;
        guestChannelToken = null;
        channelName = null;
        secretKey = null;
    }
}

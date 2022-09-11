package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.guest.dto.AuthenticationStatus;
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
    private String guestChannelToken = null;

    public AuthenticationStatus make() {

        val isClosed =
                TestHelper.getOrDefault(this.isClosed, random.bool.nextBoolean());
        val isAuthenticated = TestHelper.getOrDefault(this.isAuthenticated,
                random.bool.nextBoolean());
        val guestChannelToken = TestHelper.getOrDefault(this.guestChannelToken,
                random.string.alphanumeric());

        val authenticationStatus = new AuthenticationStatus(isClosed,
                isAuthenticated, guestChannelToken);
        reset();
        return authenticationStatus;
    }

    private void reset() {
        isClosed = false;
        isAuthenticated = null;
        guestChannelToken = null;
    }
}

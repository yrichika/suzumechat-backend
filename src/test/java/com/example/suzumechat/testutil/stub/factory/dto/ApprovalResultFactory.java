package com.example.suzumechat.testutil.stub.factory.dto;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.channel.dto.ApprovalResult;
import com.example.suzumechat.service.guest.dto.message.AuthenticationStatus;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class ApprovalResultFactory {
    @Autowired
    private TestRandom random;
    @Autowired
    private AuthenticationStatusFactory authenticationStatusFactory;

    private String joinChannelToken;
    private AuthenticationStatus authenticationStatus;

    public ApprovalResult make() {
        val joinChannelToken = TestHelper.getOrDefault(this.joinChannelToken, random.string.alphanumeric());
        val authenticationStatus =
            TestHelper.getOrDefault(this.authenticationStatus, authenticationStatusFactory.make());
        val approvalResut = new ApprovalResult(joinChannelToken, authenticationStatus);
        reset();
        return approvalResut;
    }

    private void reset() {
        joinChannelToken = null;
        authenticationStatus = null;
    }

}

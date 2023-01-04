package com.example.suzumechat.service.channel.application.messagehandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.dto.message.VisitorAuthStatus;
import com.example.suzumechat.service.channel.dto.message.error.ApprovalError;
import com.example.suzumechat.service.channel.service.HostMessagingService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.ApprovalResultFactory;
import com.example.suzumechat.testutil.stub.factory.dto.VisitorAuthStatusFactory;
import com.example.suzumechat.utility.messaging.MessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class VisitorAuthStatusUseCaseImplTests {

    @MockBean
    private HostMessagingService hostMessagingService;
    @MockBean
    private ObjectMapper mapper;
    @MockBean
    private MessageSender messageSender;

    @InjectMocks
    private VisitorAuthStatusUseCaseImpl useCase;

    @Autowired
    private TestRandom testRandom;
    @Autowired
    private ApprovalResultFactory approvalResultFactory;
    @Autowired
    private VisitorAuthStatusFactory visitorsAuthStatusFactory;

    @Test
    public void handle_should_send_approval_result() throws Exception {

        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val approvalResult = approvalResultFactory.make();
        val messageJsonStub = "{}";
        val visitorAuthStatus = visitorsAuthStatusFactory.make();
        val returnMessage = "{}";

        when(mapper.readValue(messageJsonStub, VisitorAuthStatus.class))
            .thenReturn(visitorAuthStatus);
        when(hostMessagingService.handleApproval(
            hostId,
            hostChannelToken,
            visitorAuthStatus.visitorId(),
            visitorAuthStatus.isAuthenticated()))
                .thenReturn(Optional.of(approvalResult));
        when(mapper.writeValueAsString(approvalResult.authenticationStatus()))
            .thenReturn(returnMessage);

        useCase.handle(hostId, hostChannelToken, messageJsonStub);

        verify(messageSender, times(1)).toVisitor(
            approvalResult.joinChannelToken(),
            visitorAuthStatus.visitorId(),
            returnMessage);
    }

    @Test
    public void handle_should_send_message_back_to_host_if() throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val messageJsonStub = "{}";
        val visitorAuthStatus = visitorsAuthStatusFactory.make();

        when(mapper.readValue(messageJsonStub, VisitorAuthStatus.class))
            .thenReturn(visitorAuthStatus);
        when(hostMessagingService.handleApproval(
            hostId,
            hostChannelToken,
            visitorAuthStatus.visitorId(),
            visitorAuthStatus.isAuthenticated()))
                .thenReturn(Optional.empty());

        useCase.handle(hostId, hostChannelToken, messageJsonStub);

        verify(messageSender, times(1))
            .returningToHost(eq(hostChannelToken), any(ApprovalError.class));
    }
}

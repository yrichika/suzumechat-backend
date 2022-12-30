package com.example.suzumechat.service.guest.application.messagehandler.visitor;

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
import com.example.suzumechat.service.guest.dto.message.JoinRequest;
import com.example.suzumechat.service.guest.dto.message.JoinRequestClosed;
import com.example.suzumechat.service.guest.dto.message.error.JoinRequestError;
import com.example.suzumechat.service.guest.service.VisitorMessagingService;
import com.example.suzumechat.service.valueobject.ChannelToken;
import com.example.suzumechat.service.valueobject.EmptyChannelToken;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.dto.JoinRequestFactory;
import com.example.suzumechat.utility.messaging.MessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class JoinRequestUseCaseImplTests {
    @MockBean
    private VisitorMessagingService messagingService;
    @MockBean
    private ObjectMapper mapper;
    @MockBean
    private MessageSender messageSender;

    @InjectMocks
    private JoinRequestUseCaseImpl useCase;

    @Autowired
    private TestRandom testRandom;
    @Autowired
    private JoinRequestFactory joinRequestFactory;


    @Test
    public void handle_should_send_joinRequest_to_host() throws Exception {

        val joinChannelToken = testRandom.string.alphanumeric();
        val jsonMessageStub = "{}";
        val joinRequest = joinRequestFactory.make();
        val hostChannelToken = testRandom.string.alphanumeric();
        val hostChannelTokenTyped = new ChannelToken(hostChannelToken);

        when(mapper.readValue(jsonMessageStub, JoinRequest.class)).thenReturn(joinRequest);
        when(messagingService.createGuestAsVisitor(
            joinChannelToken,
            joinRequest.visitorId(),
            joinRequest.visitorPublicKey(),
            joinRequest.whoIAmEnc()))
                .thenReturn(Optional.of(hostChannelTokenTyped));

        useCase.handle(joinChannelToken, jsonMessageStub);

        verify(messageSender, times(1)).toHost(hostChannelToken, joinRequest);
    }

    @Test
    public void handle_should_send_JoinRequestClosed_back_to_visitor_if_join_request_closed() throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val jsonMessageStub = "{}";
        val joinRequest = joinRequestFactory.make();
        val emptyTokenTyped = new EmptyChannelToken();

        when(mapper.readValue(jsonMessageStub, JoinRequest.class)).thenReturn(joinRequest);
        when(messagingService.createGuestAsVisitor(
            joinChannelToken,
            joinRequest.visitorId(),
            joinRequest.visitorPublicKey(),
            joinRequest.whoIAmEnc()))
                .thenReturn(Optional.of(emptyTokenTyped));

        useCase.handle(joinChannelToken, jsonMessageStub);

        verify(messageSender, times(1))
            .toVisitor(
                joinChannelToken,
                joinRequest.visitorId(),
                new JoinRequestClosed(true));
    }

    @Test
    public void handle_should_return_error_message_to_visitor_if_hostChannelToken_missing() throws Exception {
        val joinChannelToken = testRandom.string.alphanumeric();
        val jsonMessageStub = "{}";
        val joinRequest = joinRequestFactory.make();

        when(mapper.readValue(jsonMessageStub, JoinRequest.class)).thenReturn(joinRequest);
        when(messagingService.createGuestAsVisitor(
            joinChannelToken,
            joinRequest.visitorId(),
            joinRequest.visitorPublicKey(),
            joinRequest.whoIAmEnc()))
                .thenReturn(Optional.empty());

        useCase.handle(joinChannelToken, jsonMessageStub);

        verify(messageSender, times(1))
            .returningToVisitor(
                eq(joinChannelToken),
                eq(joinRequest.visitorId()),
                any(JoinRequestError.class));
    }
}

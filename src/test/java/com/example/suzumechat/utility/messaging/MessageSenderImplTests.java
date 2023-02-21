package com.example.suzumechat.utility.messaging;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.utility.dto.message.ErrorMessage;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class MessageSenderImplTests {

    @MockBean
    private SimpMessagingTemplate template;

    @InjectMocks
    private MessageSenderImpl sender;

    @Autowired
    private TestRandom testRandom;

    // FIXME: get from config
    final String hostEndpointPrefix = "/receive/host/";
    final String guestEndpointPrefix = "/receive/guest/";
    final String visitorEndpointPrefix = "/receive/visitor/";

    @Test
    public void toHost_should_send_json_message_to_host_endpoint() {
        val hostChannelToken = testRandom.string.alphanumeric();
        val json = randomJson();
        val expectedEndpoint = hostEndpointPrefix + hostChannelToken;

        sender.toHost(hostChannelToken, json);

        verify(template, times(1)).convertAndSend(expectedEndpoint, json);
    }

    @Test
    public void toVisitor_should_send_json_message_to_visitor_endpoint() {
        val joinChannelToken = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();

        val json = randomJson();
        val expectedEndpoint = visitorEndpointPrefix + joinChannelToken + "/" + visitorId;

        sender.toVisitor(joinChannelToken, visitorId, json);

        verify(template, times(1)).convertAndSend(expectedEndpoint, json);
    }

    @Test
    public void toGuest_should_send_json_message_to_guest_endpoint() {
        val guestChannelToken = testRandom.string.alphanumeric();
        val json = randomJson();
        val expectedEndpoint = guestEndpointPrefix + guestChannelToken;

        sender.toGuest(guestChannelToken, json);

        verify(template, times(1)).convertAndSend(expectedEndpoint, json);
    }

    @Test
    public void broadcastToChatChannel_should_send_message_to_host_and_guests() {
        val hostChannelToken = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val json = randomJson();

        sender.broadcastToChat(hostChannelToken, guestChannelToken, json);

        verify(template, times(1)).convertAndSend(hostEndpointPrefix + hostChannelToken, json);
        verify(template, times(1)).convertAndSend(guestEndpointPrefix + guestChannelToken, json);
    }

    @Test
    public void returningToHost_should_send_error_message_to_host_endpoint() {
        val hostChannelToken = testRandom.string.alphanumeric();
        val errorMessage = new ErrorMessageStub();
        val expectedEndpoint = hostEndpointPrefix + hostChannelToken;

        sender.returningToHost(hostChannelToken, errorMessage);

        verify(template, times(1)).convertAndSend(expectedEndpoint, errorMessage);
    }

    @Test
    public void returningToVisitor_should_send_error_message_to_visitor_endpoint() {
        val joinChannelToken = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        val errorMessage = new ErrorMessageStub();
        val expectedEndpoint = visitorEndpointPrefix + joinChannelToken + "/" + visitorId;

        sender.returningToVisitor(joinChannelToken, visitorId, errorMessage);

        verify(template, times(1)).convertAndSend(expectedEndpoint, errorMessage);
    }

    @Test
    public void returningToGuest_should_send_error_message_to_host_endpoint() {
        val guestChannelToken = testRandom.string.alphanumeric();
        val errorMessage = new ErrorMessageStub();
        val expectedEndpoint = guestEndpointPrefix + guestChannelToken;

        sender.returningToGuest(guestChannelToken, errorMessage);

        verify(template, times(1)).convertAndSend(expectedEndpoint, errorMessage);
    }

    private class ErrorMessageStub implements ErrorMessage {
    }

    private String randomJson() {
        return String.format("{\"%s\":\"%s\"}", testRandom.string.alphanumeric(), testRandom.string.alphanumeric());
    }
}

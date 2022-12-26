package com.example.suzumechat.service.channel.application.messagehandler;

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
    TestRandom testRandom;

    // FIXME: get from config
    final String hostEndpointPrefix = "/receive/host/";
    final String guestEndpointPrefix = "/receive/guest/";


    @Test
    public void returningToHost_should_send_error_message_to_host_endpoint() {
        val hostChannelToken = testRandom.string.alphanumeric();
        val errorMessage = new ErrorMessageStub();
        val expectedEndpoint = hostEndpointPrefix + hostChannelToken;

        sender.returningToHost(hostChannelToken, errorMessage);

        verify(template, times(1)).convertAndSend(expectedEndpoint, errorMessage);
    }

    private class ErrorMessageStub implements ErrorMessage {
    }

    @Test
    public void toVisitor_should_send_json_message_to_visitor() {
        val joinChannelToken = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();

        val json = randomJson();
        val expectedEntpoint = String.join("/", "/receive", "visitor",
            joinChannelToken, visitorId);

        sender.toVisitor(joinChannelToken, visitorId, json);

        verify(template, times(1)).convertAndSend(expectedEntpoint, json);
    }

    @Test
    public void toGuest_should_send_json_to_guest_endpoint() {
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

        sender.broadcastToChatChannel(hostChannelToken, guestChannelToken, json);

        verify(template, times(1)).convertAndSend(hostEndpointPrefix + hostChannelToken, json);
        verify(template, times(1)).convertAndSend(guestEndpointPrefix + guestChannelToken, json);
    }

    private String randomJson() {
        return String.format("{\"%s\":\"%s\"}", testRandom.string.alphanumeric(), testRandom.string.alphanumeric());
    }
}

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
import com.example.suzumechat.service.channel.dto.message.error.ChatError;
import com.example.suzumechat.service.channel.service.HostService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class ChatMessageUseCaseImplTests {

    @MockBean
    private HostService hostService;
    @MockBean
    private MessageSender messageSender;

    @InjectMocks
    private ChatMessageUseCaseImpl useCase;

    @Autowired
    TestRandom testRandom;

    @Test
    public void handle_should_call_sender_broadcastToChatChannel() throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val json = "{}";

        when(hostService.getGuestChannelToken(hostId, hostChannelToken)).thenReturn(Optional.of(guestChannelToken));

        useCase.handle(hostId, hostChannelToken, json);

        verify(messageSender, times(1)).broadcastToChatChannel(hostChannelToken, guestChannelToken, json);
    }

    @Test
    public void handle_should_all_sender_returningToHost_if_guest_channel_token_not_found() throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val json = "{}";

        when(hostService.getGuestChannelToken(hostId, hostChannelToken)).thenReturn(Optional.empty());

        useCase.handle(hostId, hostChannelToken, json);

        // eq() is necessary when using with any()
        verify(messageSender, times(1)).returningToHost(eq(hostChannelToken), any(ChatError.class));
    }
}

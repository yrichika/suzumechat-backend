package com.example.suzumechat.service.guest.application.messagehandler.guest;

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
import com.example.suzumechat.service.guest.dto.message.error.GuestChatMessageError;
import com.example.suzumechat.service.guest.service.GuestMessagingService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.utility.messaging.MessageSender;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class GuestChatMessageUseCaseImplTests {

    @MockBean
    GuestMessagingService messagingService;
    @MockBean
    MessageSender messageSender;

    @InjectMocks
    GuestChatMessageUseCaseImpl useCase;

    @Autowired
    private TestRandom testRandom;

    @Test
    public void handle_should_call_sender_broadcastToChat() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val json = "{}";

        when(messagingService.getHostChannelToken(guestId, guestChannelToken))
            .thenReturn(Optional.of(hostChannelToken));

        useCase.handle(guestId, guestChannelToken, json);

        verify(messageSender, times(1)).broadcastToChat(hostChannelToken, guestChannelToken, json);
    }

    @Test
    public void handle_should_call_sender_returningToGuest_if_host_channel_token_not_found() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val json = "{}";

        when(messagingService.getHostChannelToken(guestId, guestChannelToken)).thenReturn(Optional.empty());

        useCase.handle(guestId, guestChannelToken, json);

        verify(messageSender, times(1)).returningToGuest(eq(guestChannelToken), any(GuestChatMessageError.class));
    }
}

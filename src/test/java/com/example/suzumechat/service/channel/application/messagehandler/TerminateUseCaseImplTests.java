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
import com.example.suzumechat.service.channel.dto.message.error.TerminationError;
import com.example.suzumechat.service.channel.service.HostMessagingService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.utility.messaging.MessageSender;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class TerminateUseCaseImplTests {

    @MockBean
    private HostMessagingService hostMessagingService;
    @MockBean
    private MessageSender messageSender;

    @InjectMocks
    private TerminateUseCaseImpl useCase;

    @Autowired
    private TestRandom testRandom;

    @Test
    public void handle_should_send_terminate_message_to_guests() throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val terminateJson = "{}";

        when(hostMessagingService.getGuestChannelToken(hostId, hostChannelToken))
            .thenReturn(Optional.of(guestChannelToken));

        useCase.handle(hostId, hostChannelToken, terminateJson);

        verify(messageSender, times(1)).toGuest(guestChannelToken, terminateJson);
    }

    @Test
    public void handle_should_return_message_to_host_if_guestChannelToken_not_found() throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val json = "{}";

        when(hostMessagingService.getGuestChannelToken(hostId, hostChannelToken)).thenReturn(Optional.empty());

        useCase.handle(hostId, hostChannelToken, json);

        verify(messageSender, times(1)).returningToHost(eq(hostChannelToken), any(TerminationError.class));

    }
}

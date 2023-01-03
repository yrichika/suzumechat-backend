package com.example.suzumechat.service.guest.application.messagehandler.guest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.utility.dto.message.Unhandled;
import com.example.suzumechat.utility.messaging.MessageSender;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class GuestUnhandledUseCaseImplTests {

    @MockBean
    MessageSender messageSender;

    @InjectMocks
    GuestUnhandledUseCaseImpl useCase;

    @Autowired
    private TestRandom testRandom;

    @Test
    public void handle_should_send_unhandled_error_back_to_guest() throws Exception {
        val guestId = testRandom.string.alphanumeric();
        val guestChannelToken = testRandom.string.alphanumeric();
        val json = "{}";

        useCase.handle(guestId, guestChannelToken, json);

        verify(messageSender, times(1)).returningToGuest(guestChannelToken, new Unhandled(json));
    }
}

package com.example.suzumechat.service.channel.application.messagehandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.dto.message.error.Unhandled;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class UnhandledUseCaseImplTests {
    @MockBean
    private MessageSender messageSender;

    @InjectMocks
    private UnhandledUseCaseImpl useCase;

    @Autowired
    TestRandom testRandom;

    @Test
    public void handle_should_send_Unhandled_back_to_host() throws Exception {
        val hostId = testRandom.string.alphanumeric();
        val hostChannelToken = testRandom.string.alphanumeric();
        val json = "{}";

        useCase.handle(hostId, hostChannelToken, json);

        verify(messageSender, times(1)).returningToHost(eq(hostChannelToken), any(Unhandled.class));
    }
}
